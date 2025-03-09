package com.smartcommunity.smart_community_platform.utils;

public class PageUtils {
    public static int normalizePage(Integer page) {
        return (page == null || page < 1) ? 1 : page;
    }

    public static int normalizeSize(Integer size, int maxSize) {
        if (size == null || size < 1) return 10;
        return Math.min(size, maxSize);
    }
}