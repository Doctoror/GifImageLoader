package com.doctoror.gifimageloader;

/**
 * Stores various configurations
 */
public final class Configuration {

    private Configuration() {

    }

    private static final int DEFAULT_CACHE_SIZE = 1024 * 1024 * 10; // 10 MiB

    public static final int LRU_CACHE_SIZE_IN_BYTES;

    static {
        final long thirdOfMemory = Runtime.getRuntime().maxMemory() / 3L;
        if (DEFAULT_CACHE_SIZE > thirdOfMemory) {
            LRU_CACHE_SIZE_IN_BYTES = (int) thirdOfMemory;
        } else {
            LRU_CACHE_SIZE_IN_BYTES = DEFAULT_CACHE_SIZE;
        }
    }
}
