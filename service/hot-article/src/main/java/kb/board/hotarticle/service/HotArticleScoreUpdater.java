package kb.board.hotarticle.service;

import kb.board.common.event.Event;
import kb.board.common.event.EventPayload;
import kb.board.hotarticle.repository.ArticleCreatedTimeRepository;
import kb.board.hotarticle.repository.HotArticleListRepository;
import kb.board.hotarticle.service.eventHandler.EventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class HotArticleScoreUpdater  {
    private final HotArticleListRepository hotArticleListRepository;
    private final HotArticleScoreCalculator hotArticleScoreCalculator;
    private final ArticleCreatedTimeRepository articleCreatedTimeRepository;

    private static final long HOT_ARTICLE_COUNT = 10;
    private static final Duration HOT_ARTICLE_TTL = Duration.ofDays(10);

    public void update(Event<EventPayload> event, EventHandler<EventPayload> eventHandler) {
        Long articleId = eventHandler.findArticleId(event);
        LocalDateTime createdTime = articleCreatedTimeRepository.read(articleId);

        if (!isArticleCreatedToday(createdTime)) {
            return;
        }

        eventHandler.handle(event);

        long score = hotArticleScoreCalculator.calculate(articleId);
        hotArticleListRepository.add(
                articleId,
                createdTime,
                score,
                HOT_ARTICLE_COUNT,
                HOT_ARTICLE_TTL
        );
    }

    private boolean isArticleCreatedToday(LocalDateTime createdTime) {
        log.info("✅ createdTime = {}, now = {}", createdTime, LocalDateTime.now());
        return createdTime != null && createdTime.toLocalDate().equals(LocalDate.now());
    }
}
