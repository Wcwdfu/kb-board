package kb.board.articleread.service.eventhandler;

import kb.board.articleread.repository.ArticleIdListRepository;
import kb.board.articleread.repository.ArticleQueryModelRepository;
import kb.board.articleread.repository.BoardArticleCountRepository;
import kb.board.common.event.Event;
import kb.board.common.event.EventType;
import kb.board.common.event.payload.ArticleDeletedEventPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleDeletedEventHandler implements EventHandler<ArticleDeletedEventPayload> {
    private final ArticleIdListRepository articleIdListRepository;
    private final ArticleQueryModelRepository articleQueryModelRepository;
    private final BoardArticleCountRepository boardArticleCountRepository;

    @Override
    public void handle(Event<ArticleDeletedEventPayload> event) {
        ArticleDeletedEventPayload payload = event.getPayload();
        articleIdListRepository.delete(payload.getBoardId(), payload.getArticleId());
        articleQueryModelRepository.delete(payload.getArticleId());
        boardArticleCountRepository.createOrUpdate(payload.getBoardId(), payload.getBoardArticleCount());
        /**순서가 중요하다. 찰나의 순간 이긴 하지만,
         * 만약 articleQueryModel.delete -> articleIdListRepository.delete 순이면,
         * articleQueryModel이 삭제된 시점에 유저가 게시글 목록을 조회하면 삭제된 게시글이 조회될 수 있다.
         */

    }

    @Override
    public boolean supports(Event<ArticleDeletedEventPayload> event) {
        return EventType.ARTICLE_DELETED == event.getType();
    }
}
