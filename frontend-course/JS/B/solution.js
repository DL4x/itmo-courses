'use strict'

function assert (value) {
    if (typeof value !== 'number') {
        throw new Error('the argument is not a number');
    }
}

function calc (value) {
    assert(value);

    let x = value;

    function calcImpl (symbol, value) {
        assert(value);

        switch (symbol) {
            case '+':
                x += value;
                break;
            case '-':
                x -= value;
                break;
            case '*':
                x *= value;
                break;
            case '/':
                x /= value;
                break;
            case '%':
                x %= value;
                break;
            case '**':
                x **= value;
                break;
            default:
                throw new Error('unsupported sign');
        }

        return calcImpl;
    }

    calcImpl.valueOf = () => x;

    return calcImpl;
}

module.exports = calc;
