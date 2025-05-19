package kb.board.article.api;

import kb.board.article.service.request.ArticleCreateRequest;
import kb.board.article.service.response.ArticlePageResponse;
import kb.board.article.service.response.ArticleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

public class ArticleApiTest {
    RestClient client = RestClient.create("http://localhost:9000");

    @Test
    void createTest(){
        ArticleResponse response = create(new ArticleCreateRequest(
                "hi", "my content", 1L, 2L
        ));
        System.out.println("response = " + response);
    }

    ArticleResponse create(ArticleCreateRequest request){
        return client.post()
                .uri("/v1/articles")
                .body(request)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void readTest(){
        ArticleResponse response = read(182841144407867392L);
        System.out.println("response = " + response);
    }

    ArticleResponse read(Long articleId){
        return client.get()
                .uri("/v1/articles/{articleId}", articleId)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void updateTest(){
        update(176357604956528640L);
        ArticleResponse response = read(176357604956528640L);
        System.out.println("response = " + response);
    }

    void update(Long articleId) {
        client.put()
                .uri("/v1/articles/{articleId}", articleId)
                .body(new ArticleUpdateRequest("hi 2", "my content 22"))
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void deleteTest(){
        client.delete()
                .uri("/v1/articles/{articleId}", 182841144407867392L)
                .retrieve()
                .body(Void.class);
    }

    @Test
    void readAllTest(){
        ArticlePageResponse response = client.get()
                .uri("/v1/articles?boardId=1&pageSize=30&page=50000")
                .retrieve()
                .body(ArticlePageResponse.class);

        System.out.println("response.getArticleCount() = " + response.getArticleCount());
        for (ArticleResponse article : response.getArticles()) {
            System.out.println("articleId = " + article.getArticleId());
        }
    }

    @Test
    void readAllInfiniteScrollTest(){
        List<ArticleResponse> articles1 = client.get()
                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
                });

        System.out.println("firstPage");
        for (ArticleResponse articleResponse : articles1) {
            System.out.println("articleResponse.getArticleId() = " +articleResponse.getArticleId());
        }

        Long lastArticleId = articles1.getLast().getArticleId();
        System.out.println("lastArticleId = " + lastArticleId);
        List<ArticleResponse> articles2 = client.get()
                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5&lastArticleId=%s" .formatted(lastArticleId))
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
                });

        System.out.println("SecondPage");
        for (ArticleResponse articleResponse : articles2) {
            System.out.println("articleResponse.getArticleId() = " +articleResponse.getArticleId());
        }
    }

    @Test
    void countTest() {
        ArticleResponse response = create(new ArticleCreateRequest("hi", "content", 1L, 2L));

        Long count1 = client.get()
                .uri("/v1/articles/boards/{boardId}/count", 2L)
                .retrieve()
                .body(Long.class);
        System.out.println("count1 = " + count1); // 1

        client.delete()
                .uri("/v1/articles/{articleId}", response.getArticleId())
                .retrieve()
                .body(Void.class);

        Long count2 = client.get()
                .uri("/v1/articles/boards/{boardId}/count", 2L)
                .retrieve()
                .body(Long.class);
        System.out.println("count2 = " + count2); // 0
    }

    @Getter
    @AllArgsConstructor
    static class ArticleCreateRequest {
        private String title;
        private String content;
        private Long writerId;
        private Long boardId;
    }

    @Getter
    @AllArgsConstructor
    static class ArticleUpdateRequest {
        private String title;
        private String content;
    }
}
