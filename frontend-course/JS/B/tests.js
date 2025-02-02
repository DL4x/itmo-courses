'use strict'

const calc = require('./solution');

if (typeof calc(1) !== 'function') {
    throw '"calc" должен быть функцией';
}

if (calc(1) + 1 !== 2) {
    throw 'Неверное значение арифметической операции';
}

if (calc(1) + 1 !== 2) {
    throw 'Неверное значение арифметической операции';
}

if (calc(2) + calc(2) !== 4) {
    throw 'Неверное значение арифметической операции';
}

if (typeof calc(1)('+', 2)('-', 3) !== 'function') {
    throw 'multi "calc" должен быть функцией';
}

if (calc(1)('+', 2)('-', 3) + 10 !== 10) {
    throw 'Неверное значение арифметической операции';
}

if (calc(1)('+', 2)('-', 1)('*', 2) + 0 !== 4) {
    throw 'Неверное значение арифметической операции';
}

const value = calc(1)('+', 3);
if (value('*', 3) + value('*', 2) === 12 + 8) {
    throw 'Неверное значение арифметической операции';
}

try {
    calc('Hello!');
} catch (e) {
    console.log(e.message);
}

try {
    calc(3)('$', 3);
} catch (e) {
    console.log(e.message);
}

try {
    calc(3)('+', 'Hello!');
} catch (e) {
    console.log(e.message);
}
