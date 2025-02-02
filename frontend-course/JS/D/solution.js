'use strict'

const getNewObjWithPrototype = (obj) => {
    return Object.create(obj);
}

const getEmptyObj = () => {
    return getNewObjWithPrototype(null);
}

const setPrototypeChain = ({ programmer, student, teacher, person }) => {
    Object.setPrototypeOf(programmer, student);
    Object.setPrototypeOf(student, teacher)
    Object.setPrototypeOf(teacher, person);
}

const getObjWithEnumerableProperty = () => {
    const obj =  {};

    Object.defineProperty(obj, 'name', {
        value: 'Alex',
        enumerable: false,
    });
    Object.defineProperty(obj, 'age', {
        value: 18,
        enumerable: true,
    });
    Object.defineProperty(obj, 'work', {
        value: 'empty',
        enumerable: false,
    });

    return obj;
}

const getWelcomeObject = (person) => {
    const obj = Object.create(person);
    obj.voice = function () {
        return `Hello, my name is ${person.name}. I am ${person.age}.`;
    }
    return obj;
}

class Singleton {
    constructor (id) {
        if (Singleton.instance) {
            return Singleton.instance;
        }
        this.id = id;
        Singleton.instance = this;
    }
}

const defineTimes = () => {
    Number.prototype.times = function (callback) {
        for (let i = 1; i <= this; i++) {
            callback(i, this);
        }
    }
}

function getUniq(arr) {
    return Array.from(new Set(arr));
}

const defineUniq = () => {
    Object.defineProperty(Array.prototype, 'uniq', {
        get: function () {
            return getUniq(this);
        }
    });
}

const defineUniqSelf = () => {
    Object.defineProperty(Array.prototype, 'uniqSelf', {
        get: function () {
            const uniq = getUniq(this);
            this.length = 0;
            uniq.forEach(x => {
                this.push(x);
            });
            return uniq;
        }
    });
}

module.exports = {
    getNewObjWithPrototype,
    getEmptyObj,
    setPrototypeChain,
    getObjWithEnumerableProperty,
    getWelcomeObject,
    Singleton,
    defineTimes,
    defineUniq,
    defineUniqSelf,
}
