/*
 * Copyright (C) 2015 Yaroslav Mytkalyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.doctoror.gifimageloader.sample;

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
