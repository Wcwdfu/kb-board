package kb.board.hotarticle.service.eventHandler;

import kb.board.common.event.Event;
import kb.board.common.event.EventPayload;

public interface EventHandler<T extends EventPayload> {
    void handle(Event<T> event);
    boolean supports(Event<T> event);
    Long findArticleId(Event<T> event);


}
