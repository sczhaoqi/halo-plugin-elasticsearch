package com.sczq.halo.plugin.elasticsearch.utils;

import com.sczq.halo.plugin.elasticsearch.config.ElasticSearchSetting;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import run.halo.app.search.SearchParam;
import run.halo.app.search.SearchResult;
import run.halo.app.search.post.PostHit;
import java.io.IOException;

@Slf4j
public class ElasticsearchUtilTest {
    @Test
    public void test(){
        System.out.printf("1");
    }
    // @Test
    public void testSearch() throws IOException {
        String[] highlightAttributes = {"title", "excerpt", "content"};
        ElasticSearchSetting.updateSetting("halo_es_index", "http://localhost:9200", "");
        IndexHolder.getIndex();
        MultiValueMap<String, String> query = new LinkedMultiValueMap<>();
        query.add("keyword", "hello");
        SearchParam searchParam = new SearchParam(query);
        SearchResult<PostHit>
            data = IndexHolder.getIndex().getPostHitSearchResult(searchParam,highlightAttributes);
        log.info("{}", data);
    }
}
