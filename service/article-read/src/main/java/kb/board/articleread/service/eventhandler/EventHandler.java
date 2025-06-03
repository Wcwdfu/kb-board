package kb.board.articleread.service.eventhandler;

import kb.board.common.event.Event;
import kb.board.common.event.EventPayload;

public interface EventHandler<T extends EventPayload> {
    void handle(Event<T> event);
    boolean supports(Event<T> event);
}
