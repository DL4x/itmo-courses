'use strict'

const productsButton = document.querySelector('#mws-products');
const productsMenu = document.querySelector('.mws-drop-down-menu');

let activeMenuButtonId = null;
const menuButtons = document.querySelectorAll('.mws-menu-button');

menuButtons.forEach(menuButton => {
    menuButton.addEventListener('click', () => {
        productsMenu.classList.remove('active');

        if (menuButton.id === activeMenuButtonId) {
            menuButton.classList.remove('active');
            activeMenuButtonId = null;
            return;
        }

        menuButtons.forEach(button => {
            button.classList.remove('active');
        });

        menuButton.classList.add('active');
        if (menuButton.id === productsButton.id) {
            productsMenu.classList.add('active');
        }
        activeMenuButtonId = menuButton.id;
    });
});

let activeProductButtonId = document.querySelector('.active-product').id;
const productButtons = document.querySelectorAll('.mws-product-button');
const productLists = document.querySelectorAll('.mws-product-list');

productLists.forEach(productList => {
    if (productList.id === `${activeProductButtonId}-list`) {
        productList.classList.add('active-product');
    }
});

productButtons.forEach(productButton => {
    productButton.addEventListener('click', () => {
        if (productButton.id === activeProductButtonId) {
            return;
        }

        productButtons.forEach(button => {
            button.classList.remove('active-product');
        });
        productButton.classList.add('active-product');

        productLists.forEach(productList => {
            productList.classList.remove('active-product');
        });

        const currentContentList = document.querySelector(`#${productButton.id}-list`);
        if (currentContentList) {
            currentContentList.classList.add('active-product');
        }
        activeProductButtonId = productButton.id;
    });
});
