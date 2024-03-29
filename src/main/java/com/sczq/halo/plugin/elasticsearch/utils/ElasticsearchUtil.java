package com.sczq.halo.plugin.elasticsearch.utils;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.sczq.halo.plugin.elasticsearch.bean.Document;
import com.sczq.halo.plugin.elasticsearch.config.ElasticSearchSetting;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import run.halo.app.search.SearchParam;
import run.halo.app.search.SearchResult;
import run.halo.app.search.post.PostHit;

/**
 * @author sczhaoqi
 */
public class ElasticsearchUtil {
    private ElasticsearchClient client;
    private String index;

    public ElasticsearchUtil(String index, String serverUrl, String apiKey) {
        init(index, serverUrl, apiKey);
    }

    private void init(String index, String serverUrl, String apiKey) {
        this.index = index;
        RestClientBuilder restClientBuilder = RestClient.builder(
            HttpHost.create(serverUrl)
        );

        if (StringUtils.isNotBlank(apiKey)) {
            restClientBuilder.setDefaultHeaders(new Header[] {
                new BasicHeader("Authorization", "ApiKey " + apiKey)
            });
        }
        RestClient restClient = restClientBuilder.build();
        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
            restClient, new JacksonJsonpMapper());

        // And create the API client
        client = new ElasticsearchClient(transport);
    }

    public void createIndex() throws IOException {
        client.indices().create(c -> c
            .index(index)
        );
    }

    public void deleteIndex() throws IOException {
        client.indices().delete(c -> c
            .index(index)
        );
    }

    public <T> void addDocument(String id, T data)
        throws IOException {
        client.index(i -> i
            .index(index)
            .id(id)
            .document(data)
        );
    }

    public <T> void updateDocument(String id, T data)
        throws IOException {
        Class<T> clazz = (Class<T>) data.getClass();
        client.update(u -> u
                .index(index)
                .id(id)
                .upsert(data),
            clazz
        );
    }

    public <T> void deleteDocument(String id)
        throws IOException {
        client.delete(d -> d
            .index(index)
            .id(id)
        );
    }


    public <T> T getDocument(String id, Class<T> clazz) {
        T data = null;
        try {
            GetResponse<T> response = client.get(g -> g
                    .index(index)
                    .id(id),
                clazz
            );
            if (response.found()) {
                data = response.source();
            }
        } catch (IOException ignored) {
        }
        return data;
    }


    public <T> List<Hit<T>> searchDocuments(String searchText, Integer from,
        Integer size, Class<T> clazz) {
        List<Hit<T>> result = new ArrayList<>();
        try {
            SearchResponse<T> response = client.search(s -> s
                    .index(index)
                    .query(q -> q.multiMatch(m -> m
                            .query(searchText)
                            .fields("title", "content", "excerpt")
                            .operator(Operator.Or)
                        )
                    )
                    .from(from).size(size),
                clazz
            );
            result = response.hits().hits();
        } catch (IOException ignored) {
        }
        return result;
    }

    public void reset(String index, String serverUrl, String apiKey) {
        init(index, serverUrl, apiKey);
    }

    public <T> SearchResponse<T> search(SearchRequest.Builder searchRequestBuilder, Class<T> clazz)
        throws IOException {
        SearchResponse<T> response = client.search(s -> searchRequestBuilder.index(index),
            clazz
        );
        return response;
    }

    public void addDocumentsInBatches(BulkRequest.Builder br) throws IOException {
        client.bulk(b -> br.index(index));
    }

    public void deleteDocuments(List<String> titles) throws IOException {
        client.deleteByQuery(dl -> dl.index(index).query(q -> q.terms(t -> t.field("title").terms(tm -> tm.value(titles.stream().map(a -> FieldValue.of(a)).collect(
            Collectors.toList()))))));
    }

    public void deleteAllDocuments() throws IOException {
        client.deleteByQuery(dl -> dl.index(index).query(q -> q.matchAll(m -> m)));
    }

    public void createIndexIfNeed() {
        try {
            boolean exists = client.indices().exists(e -> e.index(index)).value();
            if(!exists) {
                createIndex();
            }
        } catch (Exception ignored){
        }
    }

    public SearchResult<PostHit> getPostHitSearchResult(SearchParam searchParam, String[] highlightAttributes)
        throws IOException {
        SearchRequest.Builder searchRequest =
            new SearchRequest.Builder()
                .size(searchParam.getLimit())
                .from(0)
                .query(q -> q.multiMatch(m -> m
                        .query(searchParam.getKeyword())
                        .fields(Arrays.asList(highlightAttributes))
                        .operator(Operator.Or)
                    )
                )
                .highlight(h -> {
                        var a = h.preTags(searchParam.getHighlightPreTag())
                            .postTags(searchParam.getHighlightPostTag())
                            .fragmentSize(ElasticSearchSetting.CROP_LENGTH);
                        for (String highlightAttribute : highlightAttributes) {
                            a.fields(highlightAttribute, hh -> hh);
                        }
                        return a;
                    }
                );

        SearchResponse<Document> response = IndexHolder.getIndex()
            .search(searchRequest, Document.class);

        List<PostHit> hits = Document.convertToPostHitList(response.hits().hits());
        var result = new SearchResult<PostHit>();
        result.setHits(hits);
        result.setTotal(response.shards().total().longValue());
        result.setKeyword(searchParam.getKeyword());
        result.setLimit(searchParam.getLimit());
        result.setProcessingTimeMillis(response.took());
        return result;
    }
}
