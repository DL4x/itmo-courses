"use strict"

const cnst = (constant) => () => constant;

const one = cnst(1);

const two = cnst(2);

const variable = (variable) => (...args) => {
    switch (variable) {
        case "x":
            return args[0];
        case "y":
            return args[1];
        case "z":
            return args[2];
    }
}

const operations = foo => (...operations) => (...args) => foo(...operations.map(operation => operation(...args)));

const negate = operations((num) => -num);

const sin = operations((num) => Math.sin(num));

const cos = operations((num) => Math.cos(num));

const add = operations((num1, num2) => num1 + num2);

const subtract = operations((num1, num2) => num1 - num2);

const multiply = operations((num1, num2) => num1 * num2);

const divide = operations((num1, num2) => num1 / num2);
