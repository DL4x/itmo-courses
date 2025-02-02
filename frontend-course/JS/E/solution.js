'use strict';

const hrefRegex = /<a\s+[^>]*href="([^"]*)"/g;

class Pool {
    constructor(workers) {
        this.queue = [];
        this.count = 0;
        this.workers = workers;
    }

    #next() {
        if (this.queue.length === 0) {
            return;
        }
        if (this.workers <= this.count) {
            return;
        }
        this.count++;
        return this.queue.shift();
    }

    addTask(task) {
        return new Promise((resolve, reject) => {
            const asyncTask = async () => {
                try {
                    const result = await task();
                    resolve(result);
                } catch (e) {
                    reject(e);
                } finally {
                    this.count--;
                    const newTask = this.#next();
                    if (newTask) {
                        newTask();
                    }
                }
            };

            if (this.count < this.workers) {
                this.count++;
                asyncTask().then();
            } else {
                this.queue.push(asyncTask);
            }
        });
    }
}

async function crawlImpl(url, depth) {
    try {
        const links = [];

        const r = await fetch(url, {
            signal: AbortSignal.timeout(5000),
        });
        const content = await r.text();

        let element;
        while ((element = hrefRegex.exec(content)) !== null) {
            const link = element[1];
            if (!link) {
                continue;
            }
            if (link.startsWith('/')) {
                links.push(new URL(link, url).href);
            } else if (link.startsWith('http')) {
                links.push(link);
            }
        }

        return {
            url,
            depth,
            content,
            links: Array.from(new Set(links)),
        };
    } catch (e) {
        console.log(`Error while fetching ${url}`);
    }
}

async function crawl(startingUrl, depth, concurrency) {
    const result = [];
    let currentDepth = 1;
    let currentUrls = [startingUrl];
    const service = new Pool(concurrency);

    while (currentDepth <= depth) {
        const tasks = [];
        const nextUrls = [];
        for (const url of currentUrls) {
            tasks.push(
                service.addTask(async () => {
                    const response = await crawlImpl(
                        url,
                        currentDepth,
                    );
                    if (!response) {
                        return;
                    }
                    result.push(response);
                    nextUrls.push(...response.links);
                }).catch(console.error),
            );
        }

        await Promise.all(tasks);

        currentDepth++;
        currentUrls = Array.from(new Set(nextUrls));
    }

    return result;
}

module.exports = crawl;
