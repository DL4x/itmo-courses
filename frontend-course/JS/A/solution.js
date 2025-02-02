'use strict'

const potion = {
    name: 'Название',
    expirationDays: 5,
    created: new Date(2023, 0, 1), // 1 Января 2023.
    use: function() { },
};

function makePotionsRoom() {
    return {
        // хранилище кладовки
        store: new Map(),

        // добавляет зелье на указанную полку, метод ничего не возвращает
        add: function (shelveName, potion) {
            if (this.store.get(shelveName) === undefined) {
                this.store.set(shelveName, []);
            }
            this.store.get(shelveName).push(potion);
        },

        // Возвращает зелье, если оно есть на любой из полок. Зелье убирается из кладовки (с любой из полок, где есть зелье)
        takePotion: function (namePotion) {
            for (const potions of this.store.values()) {
                for (let i = 0; i < potions.length; i++) {
                    if (potions[i] === undefined) {
                        continue;
                    }
                    if (potions[i].name === namePotion) {
                        const potion = potions[i];
                        delete potions[i];
                        return potion;
                    }
                }
            }
        },

        // Использует зелье (вызывая у него функцию "use"). Зелье убирается из кладовки (с любой из полок, где есть зелье).
        usePotion: function (namePotion) {
            const potion = this.takePotion(namePotion);
            if (potion !== undefined) {
                potion.use();
            }
        },

        // Возвращает все зелья с полки. Содержимое полки не меняется
        getAllPotionsFromShelve: function (shelveName) {
            return this.store.get(shelveName).filter((potion) => potion !== undefined);
        },

        // Возвращает все зелья кладовки. Содержимое полок не меняется
        getAllPotions: function () {
            return Array.from(this.store.keys())
                .flatMap((shelveName) => this.getAllPotionsFromShelve(shelveName));
        },

        // Возвращает все зелья с полки. Полка остается пустой
        takeAllPotionsFromShelve: function (shelveName) {
            const allPotionsFromShelve = this.getAllPotionsFromShelve(shelveName);
            this.store.get(shelveName).length = 0;
            return allPotionsFromShelve;
        },

        // Использует все зелья с указанной полки. Полка остается пустой
        useAllPotionsFromShelve: function (shelveName) {
            const allPotionsFromShelve = this.takeAllPotionsFromShelve(shelveName);
            allPotionsFromShelve.forEach((potion) => potion.use());
        },

        // Возвращает зелья с истекшим сроком хранения. Метод убирает такие зелья из кладовки.
        // revisionDay - день (Date), в который происходит проверка сроков хранения
        clean: function(revisionDay) {
            const allExpiredPotions = [];
            for (const potions of this.store.values()) {
                for (let i = 0; i < potions.length; i++) {
                    if (potions[i] === undefined) {
                        continue;
                    }
                    const potionExpirationDate = new Date(
                        potions[i].created
                    );
                    potionExpirationDate.setDate(
                        potions[i].expirationDays +
                        potionExpirationDate.getDate()
                    );
                    if (potionExpirationDate < revisionDay) {
                        allExpiredPotions.push(potions[i]);
                        delete potions[i];
                    }
                }
            }
            return allExpiredPotions;
        },

        // возвращает число - сколько уникальных названий зелий находится в кладовке
        uniquePotionsCount() {
            return new Set(this.getAllPotions().map((potion) => potion.name)).size;
        },
    };
}

module.exports = makePotionsRoom;
