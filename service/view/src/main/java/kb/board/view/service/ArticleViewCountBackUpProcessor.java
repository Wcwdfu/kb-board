package kb.board.view.service;

import kb.board.common.event.EventType;
import kb.board.common.event.payload.ArticleViewedEventPayload;
import kb.board.common.outboxmessagerelay.OutboxEventPublisher;
import kb.board.view.entity.ArticleViewCount;
import kb.board.view.repository.ArticleViewCountBackUpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ArticleViewCountBackUpProcessor {
    private final ArticleViewCountBackUpRepository articleViewCountBackUpRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    @Transactional
    public void backUp(Long articleId, Long viewCount) {
        int result = articleViewCountBackUpRepository.updateViewCount(articleId, viewCount);

        if(result == 0){
            articleViewCountBackUpRepository.findById(articleId)
                    .ifPresentOrElse(ignored-> {},
                            ()-> articleViewCountBackUpRepository.save(
                                    ArticleViewCount.init(articleId, viewCount))
                    );
        }

        outboxEventPublisher.publish(
                EventType.ARTICLE_VIEWED,
                ArticleViewedEventPayload.builder()
                        .articleId(articleId)
                        .articleViewCount(viewCount)
                        .build(),
                articleId
        );
    }
}
