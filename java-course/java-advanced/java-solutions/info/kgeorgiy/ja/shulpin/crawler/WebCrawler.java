package info.kgeorgiy.ja.shulpin.crawler;

import info.kgeorgiy.java.advanced.crawler.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

/**
 * Craws websites, implementation of {@link Crawler}.
 *
 * @author Shulpin Egor
 */
public class WebCrawler implements NewCrawler {
    private static final int TIME_SCALE = 10;
    private static final int UNLIMITED = 100;
    private static final int DEFAULT_DEPTH = 1;

    private static int parse(String[] args, int at, int defaultVal) {
        if (args.length <= at || args[at] == null) {
            return defaultVal;
        }
        int val;
        try {
            if ((val = Integer.parseInt(args[at])) <= 0) {
                val = defaultVal;
            }
        } catch (final NumberFormatException e) {
            val = defaultVal;
            System.err.println("Warning: [depth [downloads " +
                    "[extractors [perHost]]]] must be positive numbers.");
        }
        return val;
    }

    /**
     * Allows to run Web crawler from the command line.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        if (args == null || !(1 <= args.length && args.length <= 5)) {
            System.err.println("Invalid arguments passed. " +
                    "Expected: url [depth [downloads [extractors [perHost]]]].");
            return;
        }

        final String url = args[0];
        final int depth = parse(args, 1, DEFAULT_DEPTH);
        final int downloaders = parse(args, 2, UNLIMITED);
        final int extractors = parse(args, 3, UNLIMITED);
        final int perHost = parse(args, 4, UNLIMITED);

        try (WebCrawler crawler = new WebCrawler(
                new CachingDownloader(TIME_SCALE),
                downloaders, extractors, perHost)
        ) {
            crawler.download(url, depth);
        } catch (final IOException e) {
            System.err.println("Unexpected error while downloading.");
        }
    }

    private final Downloader downloader;
    private final ExecutorService downloadersExecutorService;
    private final ExecutorService extractorsExecutorService;

    /**
     * Web crawler constructor.
     * @param downloader allows to download pages and extract links from them.
     * @param downloaders maximum number of parallel loaded pages.
     * @param extractors maximum number of parallel extracted pages.
     * @param perHost maximum number of parallel loaded pages from single host.
     */
    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        this.downloader = downloader;
        downloadersExecutorService = Executors.newFixedThreadPool(downloaders);
        extractorsExecutorService = Executors.newFixedThreadPool(extractors);
    }

    private static void moveLinks(Collection<String> currentLinks, Collection<String> nextLinks) {
        currentLinks.clear();
        currentLinks.addAll(nextLinks);
        nextLinks.clear();
    }

    private Optional<Document> downloadDocument(
            String link,
            Set<String> downloaded,
            Map<String, IOException> errors
    ) {
        Optional<Document> document = Optional.empty();
        try {
            if (!(downloaded.contains(link)
                    || errors.containsKey(link))) {
                document = Optional.ofNullable(downloader.download(link));
            }
        } catch (final IOException e) {
            errors.put(link, e);
        }
        return document;
    }

    private void extractLinks(
            int depth,
            int curDepth,
            String link,
            Document document,
            Collection<String> nextLinks,
            Collection<String> downloaded,
            Map<String, IOException> errors
    ) {
        try {
            downloaded.add(link);
            if (curDepth == depth) {
                return;
            }
            nextLinks.addAll(document.extractLinks());
        } catch (final IOException e) {
            errors.put(link, e);
        }
    }

    @Override
    public Result download(String url, int depth, Set<String> excludes) {
        final Set<String> downloaded = ConcurrentHashMap.newKeySet();
        final Map<String, IOException> errors = new ConcurrentHashMap<>();
        final Set<String> nextLinks = ConcurrentHashMap.newKeySet();
        final List<String> currentLinks = Collections
                .synchronizedList(new ArrayList<>(List.of(url)));

        Phaser phaser = new Phaser(1);

        for (int i = 1; i <= depth; i++) {
            final int curDepth = i;
            for (String link : currentLinks) {
                    phaser.register();
                    downloadersExecutorService.submit(() -> {
                        try {
                            if (excludes.stream().noneMatch(link::contains)) {
                                Optional<Document> document =
                                        downloadDocument(link, downloaded, errors);
                                if (document.isPresent()) {
                                    phaser.register();
                                    extractorsExecutorService.submit(() -> {
                                        try {
                                            extractLinks(depth, curDepth, link,
                                                    document.get(), nextLinks, downloaded, errors);
                                        } finally {
                                            phaser.arriveAndDeregister();
                                        }
                                    });
                                }
                            }
                        } finally {
                            phaser.arriveAndDeregister();
                        }
                    });
            }
            phaser.arriveAndAwaitAdvance();
            moveLinks(currentLinks, nextLinks);
        }

        return new Result(downloaded.stream().toList(), errors);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        downloadersExecutorService.shutdown();
        extractorsExecutorService.shutdown();
    }
}
