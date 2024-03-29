package com.sczq.halo.plugin.elasticsearch.config;

public class ElasticSearchSetting {

    public static final int DEFAULT_CROP_LENGTH = 80;
    public static String INDEX = "halo_es_index";

    public static String SERVER_URL = "";
    public static String API_KEY = "";
    public static int CROP_LENGTH = DEFAULT_CROP_LENGTH;

    public static void updateSetting(String index, String serverUrl, String apiKey) {
        INDEX = index;
        SERVER_URL = serverUrl;
        API_KEY = apiKey;
    }
}
