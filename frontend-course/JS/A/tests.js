const makePotionsRoom = require('./solution')

let potionsRoom;

// Можно добавлять зелья на полку

potionsRoom = makePotionsRoom();

potionsRoom.add('Полка №1', { name: 'Зелье №1'});
potionsRoom.add('Полка №1', { name: 'Зелье №2'});

if (potionsRoom.getAllPotionsFromShelve('Полка №1').length !== 2) {
    throw 'Метод "getAllPotionsFromShelve" не возвращает корректное число зелий'
}

// "Можно забрать зелье с полки"

potionsRoom = makePotionsRoom();

potionsRoom.add('Полка №1', { name: 'Зелье №1'});
potionsRoom.add('Полка №1', { name: 'Зелье №2' });

potionsRoom.takePotion('Зелье №1');

if (potionsRoom.getAllPotionsFromShelve('Полка №1').length !== 1) {
    throw 'Метод "takePotion" не уменьшает кол-во доступных зелий'
}

// Можно использовать зелье

potionsRoom = makePotionsRoom();

const makeCounter = () => {
    let count = 0;

    function innerFn () {
        ++count;
        innerFn.count = count;
    }

    innerFn.count = count;

    return innerFn;
};

const counter1 = makeCounter();
const counter2 = makeCounter();

const potion1 = { name: 'Зелье №1', use: counter1 };
const potion2 = { name: 'Зелье №2', use: counter2 };

potionsRoom.add('Полка №1', potion1);
potionsRoom.add('Полка №1', potion2);

potionsRoom.usePotion('Зелье №1');

if (counter1.count !== 1) {
    throw 'Зелье 1 не использовано'
}

if (counter2.count !== 0) {
    throw 'Зелье 2 использовано'
}
