global.fetch = require('node-fetch');

const crawl = require('./solution');

(async () => {
    const startingUrl = 'https://example.com';
    const depth = 2;
    const concurrency = 10;

    const start = performance.now();

    const result = await crawl(startingUrl, depth, concurrency);
    console.log(result);

    const final = performance.now();

    console.log(`Time = ${final - start} milliseconds`);
})();
