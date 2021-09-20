package com.example.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author zhangdj
 * @Date 2021/6/17:16:29
 */
public class CacheTest {
    Cache<Long, List<String>> cache =
        CacheBuilder.newBuilder().maximumSize(100)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();

    Cache<String, Map<String, String>> cache1 =
            CacheBuilder.newBuilder().maximumSize(100)
                    .expireAfterWrite(10, TimeUnit.MINUTES)
                    .build();
}