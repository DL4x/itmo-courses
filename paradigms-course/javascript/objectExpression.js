"use strict"

function ExpressionElement(operation, evaluate, toString, prefix) {
    operation.prototype.evaluate = evaluate;
    operation.prototype.toString = toString;
    operation.prototype.prefix = prefix;
}

function Const(constant) {
    this.constant = constant;
}
ExpressionElement(
    Const,
    function () {
        return this.constant;
    },
    function () {
        return `${this.constant}`;
    },
    function () {
        return `${this.constant}`;
    }
);

function Variable(variable) {
    this.variable = variable;
}
ExpressionElement(
    Variable,
    function (...args) {
        switch (this.variable) {
            case "x":
                return args[0];
            case "y":
                return args[1];
            case "z":
                return args[2];
        }
    },
    function () {
        return this.variable;
    },
    function () {
        return this.variable;
    }
);

function AbstractOperation(...operations) {
    this.operations = operations;
}
ExpressionElement(
    AbstractOperation,
    function (...args) {
        return this.foo(...this.operations.map(operation => operation.evaluate(...args)));
    },
    function () {
        return this.operations.join(" ") + " " + this.mathSymbol;
    },
    function () {
        return "(" + this.mathSymbol + " " + this.operations.map(operation => operation.prefix()).join(" ") + ")";
    }
);

let operationSymbol = [];
let operationLength = [];
function equate(foo, mathSymbol, number) {
    const operation = function (...operations) {
        AbstractOperation.call(this, ...operations);
    }
    operation.prototype = Object.create(AbstractOperation.prototype);
    operation.prototype.constructor = operation;
    operation.prototype.foo = foo;
    operation.prototype.mathSymbol = mathSymbol;
    operationSymbol[mathSymbol] = operation;
    operationLength[mathSymbol] = number;
    return operation;
}

const Negate = equate(num => -num, "negate", 1);

const Add = equate((num1, num2) => (num1 + num2), "+", 2);

const Subtract = equate((num1, num2) => num1 - num2, "-", 2);

const Multiply = equate((num1, num2) => (num1 * num2), "*", 2);

const Divide = equate((num1, num2) => (num1 / num2), "/", 2);

const Exp = equate(Math.exp, "exp", 1);

const Ln = equate(Math.log, "ln", 1);

const Sum = equate((...args) => args.reduce((result, num) => result + num, 0), "sum", Infinity);

const Avg = equate((...args) => args.reduce((result, num) => result + (num / args.length), 0), "avg", Infinity);

function parse(expression) {
    let stack = [];
    for (const element of expression.trim().split(/\s+/)) {
        if (element in operationSymbol) {
            stack.push(new operationSymbol[element](...stack.splice(stack.length - operationLength[element])));
        } else if (element === "x" || element === "y" || element === "z") {
            stack.push(new Variable(element));
        } else {
            stack.push(new Const(parseInt(element)));
        }
    }
    return stack.pop();
}

function AbstractError() {
    const error = function (message) {
        AbstractError.call(this, message);
        this.message = message;
    }
    error.prototype = Object.create(Error.prototype);
    error.prototype.constructor = error;
    return error;
}

const MissedElementError = AbstractError();

const UnknownElementError = AbstractError();

const IncorrectEntryError = AbstractError();

const InvalidArgumentsNumberError = AbstractError();

function StringSource(string) {
    let pos = 0;
    this.string = string;

    StringSource.prototype.hasNext = function () {
        return pos < this.string.length;
    }

    StringSource.prototype.next = function () {
        return this.string.charAt(pos++);
    }
}

function BaseParser(string) {
    const source = new StringSource(string);
    const END = '\0';
    let ch = source.hasNext() ? source.next() : END;

    BaseParser.prototype.take = function () {
        const result = ch;
        ch = source.hasNext() ? source.next() : END;
        return result;
    }

    BaseParser.prototype.actual = function (expected) {
        return ch === expected;
    }

    BaseParser.prototype.get = function (expected) {
        if (this.actual(expected)) {
            this.take();
            return true;
        }
        return false;
    }

    BaseParser.prototype.between = function (from, to) {
        return from <= ch && ch <= to;
    }

    BaseParser.prototype.skip = function () {
        while (this.between(String.fromCharCode(1), String.fromCharCode(32))) {
            this.take();
        }
    }

    BaseParser.prototype.eof = function () {
        return this.actual(END);
    }
}

function parsePrefix(stringExpression) {
    if (stringExpression.length === 0) {
        throw new IncorrectEntryError(
            "There is no expression to calculate! Try to write the expression correctly."
        );
    }
    const parser = new BaseParser(stringExpression.trim());
    let expression = parseElement(parser);
    if (!parser.eof()) {
        throw new IncorrectEntryError(
            "Incorrect entry for the expression " + stringExpression + " on the element " + expression.prefix() + parser.take()
        );
    }
    return expression;
}

function parseElement(parser) {
    parser.skip();
    const result = parseValue(parser);
    parser.skip();
    return result;
}

function parseValue(parser) {
    if (parser.get("(")) {
        return parseBrackets(parser);
    }
    if (parser.actual(")")) {
        throw new MissedElementError("The expression omitted the '('.");
    }
    if (parser.between("a", "z") || parser.between("A", "Z")) {
        return parseVariable(parser);
    }
    if (parser.actual("-") || parser.between("0", "9")) {
        return parseConst(parser)
    }
    throw new UnknownElementError("Unsupported element " + parser.take() + " in the expression.");
}

function parseConst(parser) {
    if (parser.get("0")) {
        return new Const(0);
    }
    let result = [parser.actual("-") ? parser.take() : ""];
    while (parser.between("0", "9")) {
        result.push(parser.take());
    }
    if (parser.between("a", "z") || parser.between("A", "Z")) {
        throw new IncorrectEntryError(
            "Incorrect entry " + result.join("") + parser.take() + "."
        );
    }
    return new Const(parseInt(result.join("")));
}

function parseVariable(parser) {
    if (!parser.between("x", "z")) {
        throw new UnknownElementError(
            "Unknown variable " + "'" + parser.take() + "'" + "."
        );
    }
    const result = parser.take();
    if (parser.between("0", "9") || parser.between("a", "z") || parser.between("A", "Z")) {
        throw new IncorrectEntryError(
            "Incorrect entry " + result + parser.take() + "."
        );
    }
    return new Variable(result);
}

function parseBrackets(parser) {
    const operation = parseOperation(parser);
    if (!(operation in operationSymbol)) {
        throw new IncorrectEntryError(
            "The operation is missing or recorded incorrectly " + "'" + operation + "'."
        );
    }
    let elements = [];
    parser.skip();
    while (!parser.get(")")) {
        elements.push(parseElement(parser));
    }
    let expression = new operationSymbol[operation](...elements);
    if (elements.length < operationLength[operation] && operationLength[operation] !== Infinity) {
        throw new InvalidArgumentsNumberError(
            "Not enough arguments in operation " + expression.prefix() + "."
        );
    }
    if (elements.length > operationLength[operation] && operationLength[operation] !== Infinity) {
        throw new InvalidArgumentsNumberError(
            "Too many arguments in operation " + expression.prefix() + "."
        );
    }
    return expression;
}

function parseOperation(parser) {
    parser.skip();
    if (parser.actual("+") ||
        parser.actual("-") ||
        parser.actual("*") ||
        parser.actual("/")) {
        const result = parser.take();
        if (parser.between("0", "9")) {
            throw new IncorrectEntryError("Incorrect entry for operation " + result + parser.take());
        }
        return result;
    }
    let operation = [];
    while (!parser.between(String.fromCharCode(0), String.fromCharCode(32)) && !parser.actual("(")) {
        operation.push(parser.take());
    }
    return operation.join("");
}
