package com.sczq.halo.plugin.elasticsearch.utils;

import com.sczq.halo.plugin.elasticsearch.config.ElasticSearchSetting;

public class IndexHolder {
    private volatile static ElasticsearchUtil index;

    public static ElasticsearchUtil getIndex() {
        if (index == null) {
            synchronized (IndexHolder.class) {
                if (index == null) {
                    index = generateIndex();
                }
                index.createIndexIfNeed();
            }
        }
        return index;
    }

    public static void resetIndex() {
        index = null;
    }

    private static ElasticsearchUtil generateIndex() {
        return new ElasticsearchUtil(ElasticSearchSetting.INDEX, ElasticSearchSetting.SERVER_URL, ElasticSearchSetting.API_KEY);
    }
}
