package com.sczq.halo.plugin.elasticsearch.bean;

import co.elastic.clients.elasticsearch.core.search.Hit;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.sczq.halo.plugin.elasticsearch.utils.BeanUtils;
import lombok.Data;
import run.halo.app.search.post.PostDoc;
import run.halo.app.search.post.PostHit;

@Data
public class Document {
    private String name;
    private String title;
    private String excerpt;
    private String content;
    private String publishTimestamp;
    private String permalink;

    public static Document convertFromPostDoc(PostDoc post) {
        var doc = new Document();
        doc.name = post.name();
        doc.title = post.title();
        doc.excerpt = post.excerpt();
        doc.content = post.content();
        doc.publishTimestamp = post.publishTimestamp().toString();
        doc.permalink = post.permalink();
        return doc;
    }

    public static List<Document> convertFromPostDocList(List<PostDoc> posts) {
        return posts.stream().map(Document::convertFromPostDoc).toList();
    }

    public PostHit convertToPostHit() {
        var hit = new PostHit();
        hit.setName(name);
        hit.setTitle(title);
        hit.setContent(content);
        hit.setPublishTimestamp(Instant.parse(publishTimestamp));
        hit.setPermalink(permalink);
        return hit;
    }

    public static List<PostHit> convertToPostHitList(List<Hit<Document>> docs) {
        return docs.stream().map(
            hit -> {
                Document source = hit.source();
                Map<String, List<String>> highlights = hit.highlight();
                Map<String, String> newProps = new HashMap<>();
                highlights.forEach((k, v) -> {
                    newProps.put(k, String.join("...", v));
                });
                BeanUtils.copyProperties(source, newProps);
                return source;
            }
        ).map(Document::convertToPostHit).toList();
    }
}
