package kb.board.comment.service.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class CommentCreateRequest {
    private Long articleId;
    private String content;
    private Long parentCommentId;
    private Long writerId;
}
