const protoSolutions = require('./solution');
const {Singleton} = require("./solution");

const obj = { name: 'Egor' };
if (protoSolutions.getNewObjWithPrototype(obj).__proto__ !== obj) {
    console.log('Incorrect prototype!');
}

const emptyObj = protoSolutions.getEmptyObj();
if (emptyObj.properties !== undefined) {
    console.log('Object must be empty!');
}
if (emptyObj.__proto__ !== undefined) {
    console.log('Object prototype must be empty!');
}

const programmer = {
    language: 'JavaScript'
};
const student = {
    specialization: 'Frontend Development'
};
const teacher = {
    subject: 'Programming'
};
const person = {
    age: 25
};
protoSolutions.setPrototypeChain({
    programmer,
    student,
    teacher,
    person
});
if (programmer.subject !== 'Programming') {
    console.log('Incorrect prototype setter!');
}
if (student.age !== 25) {
    console.log('Incorrect prototype setter!');
}

const enumObj = protoSolutions.getObjWithEnumerableProperty()
const set = new Set();
for (const key in enumObj) {
    set.add(key);
}
if (set.size !== 1 || !set.has('age')) {
    console.log('Incorrect enumerable properties!');
}

const protoPerson = {
    name: 'Egor',
    age: 19,
}
const welcomeObj = protoSolutions.getWelcomeObject(protoPerson);
if (welcomeObj.__proto__ !== protoPerson) {
    console.log('Incorrect prototype!');
}
if (welcomeObj.voice() !== 'Hello, my name is Egor. I am 19.') {
    console.log('Incorrect voice function!');
}

const single1 = new Singleton(0);
const single2 = new Singleton(3);
if (single1 !== single2) {
    console.log('Incorrect singleton!');
}

protoSolutions.defineTimes();
const count = 5;
count.times((index, value) => console.log(index, value));

protoSolutions.defineUniq();
const arr1 = [1,2,2];
console.log(arr1.uniq);
console.log(arr1);

protoSolutions.defineUniqSelf();
const arr2 = [1,2,2];
console.log(arr2.uniqSelf);
console.log(arr2);
