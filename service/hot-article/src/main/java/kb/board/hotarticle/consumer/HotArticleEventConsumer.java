package kb.board.hotarticle.consumer;

import kb.board.common.event.Event;
import kb.board.common.event.EventPayload;
import kb.board.common.event.EventType;
import kb.board.hotarticle.service.HotArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HotArticleEventConsumer {
    private final HotArticleService hotArticleService;

    @KafkaListener(topics = {
            EventType.Topic.KB_BOARD_ARTICLE,
            EventType.Topic.KB_BOARD_COMMENT,
            EventType.Topic.KB_BOARD_LIKE,
            EventType.Topic.KB_BOARD_VIEW
    })
    public void listen(String msg, Acknowledgment ack) {
        log.info("[HotArticleEventConsumer.listen] received msg: {}", msg);
        Event<EventPayload> event = Event.fromJson(msg);
        if (event != null) {
            hotArticleService.handleEvent(event);
        }
        ack.acknowledge();
    }

}
