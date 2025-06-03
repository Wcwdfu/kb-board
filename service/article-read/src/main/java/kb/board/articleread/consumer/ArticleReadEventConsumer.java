package kb.board.articleread.consumer;

import kb.board.articleread.service.ArticleReadService;
import kb.board.common.event.Event;
import kb.board.common.event.EventPayload;
import kb.board.common.event.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleReadEventConsumer {
    private final ArticleReadService articleReadService;

    @KafkaListener(topics = {
            EventType.Topic.KB_BOARD_ARTICLE,
            EventType.Topic.KB_BOARD_COMMENT,
            EventType.Topic.KB_BOARD_LIKE
    })
    public void listen(String msg, Acknowledgment ack) {
        log.info("[ArticleReadEventConsumer.listen] msg={}", msg);
        Event<EventPayload> event = Event.fromJson(msg);
        if (event != null) {
            articleReadService.handleEvent(event);
        }
        ack.acknowledge();
    }
}
