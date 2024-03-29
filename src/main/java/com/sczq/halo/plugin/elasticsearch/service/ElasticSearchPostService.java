package com.sczq.halo.plugin.elasticsearch.service;

import co.elastic.clients.elasticsearch.core.BulkRequest;
import com.sczq.halo.plugin.elasticsearch.bean.Document;
import com.sczq.halo.plugin.elasticsearch.config.ElasticSearchSetting;
import com.sczq.halo.plugin.elasticsearch.utils.IndexHolder;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import run.halo.app.search.SearchParam;
import run.halo.app.search.SearchResult;
import run.halo.app.search.post.PostDoc;
import run.halo.app.search.post.PostHit;
import run.halo.app.search.post.PostSearchService;

@Slf4j
@Service
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ElasticSearchPostService implements PostSearchService {

    private static final String[] highlightAttributes = {"title", "excerpt", "content"};
    private static final String[] cropAttributes = {"excerpt", "content"};

    @Override
    public SearchResult<PostHit> search(SearchParam searchParam) throws Exception {
        log.info("search keyword: {}", searchParam.getKeyword());

         SearchResult<PostHit>
             result = IndexHolder.getIndex().getPostHitSearchResult(searchParam, highlightAttributes);

         log.info("search result: {}", result);
        return result;
    }



    @Override
    public void addDocuments(List<PostDoc> list) {
        List<Document> documents = list.stream().map(Document::convertFromPostDoc).toList();
        log.info("add documents: {}", documents);
        try {
            BulkRequest.Builder bk = new BulkRequest.Builder();
            documents.forEach(doc -> {
                bk.operations(op -> op.index(
                    idx -> idx.index(ElasticSearchSetting.INDEX).id(doc.getTitle()).document(doc)));
            });
            IndexHolder.getIndex().addDocumentsInBatches(bk);
        } catch (IOException e) {
            log.error("add documents error, documents: {}", documents, e);
        }
    }

    @Override
    public void removeDocuments(Set<String> names) throws Exception {
        log.info("remove documents: {}", names);
        IndexHolder.getIndex().deleteDocuments(names.stream().toList());
    }

    @Override
    public void removeAllDocuments() throws Exception {
        log.info("remove all documents");
        IndexHolder.getIndex().deleteAllDocuments();
    }
}