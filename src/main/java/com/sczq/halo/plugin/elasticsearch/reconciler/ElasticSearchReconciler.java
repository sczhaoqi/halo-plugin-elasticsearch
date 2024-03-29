package com.sczq.halo.plugin.elasticsearch.reconciler;

import com.sczq.halo.plugin.elasticsearch.config.ElasticSearchSetting;
import com.sczq.halo.plugin.elasticsearch.utils.IndexHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.halo.app.extension.ConfigMap;
import run.halo.app.extension.controller.Controller;
import run.halo.app.extension.controller.ControllerBuilder;
import run.halo.app.extension.controller.Reconciler;
import run.halo.app.plugin.ReactiveSettingFetcher;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticSearchReconciler implements Reconciler<Reconciler.Request> {

    private final ReactiveSettingFetcher settingFetcher;

    public static final String DEFAULT_EMPTY_STRING = "";

    @Override
    public Result reconcile(Request request) {
        String name = request.name();
        if (!isElasticSearchSetting(name)) {
            return Result.doNotRetry();
        }
        loadPluginSetting();
        return Result.doNotRetry();
    }

    private void loadPluginSetting() {
        settingFetcher.get("base")
            .doOnSuccess(baseSetting -> {
                log.info("ElasticSearch setting update: {}", baseSetting);
                ElasticSearchSetting.updateSetting(
                    baseSetting.path("index").asText(DEFAULT_EMPTY_STRING),
                    baseSetting.path("serverUrl").asText(DEFAULT_EMPTY_STRING),
                    baseSetting.path("apiKey").asText(DEFAULT_EMPTY_STRING));
                IndexHolder.resetIndex();
            }).subscribe();
    }

    @Override
    public Controller setupWith(ControllerBuilder builder) {
        return builder.extension(new ConfigMap()).build();
    }

    private boolean isElasticSearchSetting(String name) {
        return "elasticsearch-configmap".equals(name);
    }
}