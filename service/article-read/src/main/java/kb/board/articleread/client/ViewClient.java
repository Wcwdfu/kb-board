package kb.board.articleread.client;

import jakarta.annotation.PostConstruct;
import kb.board.articleread.cache.OptimizedCacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViewClient {
    private RestClient restClient;
    @Value("${endpoints.kb-board-view-service.url}")
    private String viewServiceUrl;

    @PostConstruct
    public void initRestClient() {
        restClient = RestClient.create(viewServiceUrl);
    }

    //@Cacheable(key = "#articleId", value = "articleViewCount")
    @OptimizedCacheable(type = "articleViewCount", ttlSeconds = 1)
    public long count(Long articleId) {
        log.info("[ViewClient.count] articleId: {}", articleId);
        try {
            return restClient.get()
                    .uri("/v1/article-views/articles/{articleId}/count", articleId)
                    .retrieve()
                    .body(Long.class);
        } catch (Exception e) {
            log.error("[ViewClient.count] articleId={}", articleId, e);
            return 0;
        }
    }

}
