package kb.board.comment.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentPath {
    private String path;

    private static final String CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static final int DEPTH_CHUNK_SIZE = 5;
    private static final int MAX_DEPTH = 5;

    private static final String MIN_CHUNK = String.valueOf(CHARSET.charAt(0)).repeat(DEPTH_CHUNK_SIZE);
    private static final String MAX_CHUNK = String.valueOf(CHARSET.length()-1).repeat(DEPTH_CHUNK_SIZE);

    public static CommentPath create(String path) {
        if (isDepthOverflowed(path)) {
            throw new IllegalArgumentException("Depth Overflowed");
        }
        CommentPath commentPath = new CommentPath();
        commentPath.path = path;
        return commentPath;
    }

    private static boolean isDepthOverflowed(String path) {
        return calDepth(path) > MAX_DEPTH;
    }

    private static int calDepth(String path) {
        return path.length() / DEPTH_CHUNK_SIZE;
    }

    public int getDepth() {
        return calDepth(path);
    }

    public boolean isRoot() {
        return calDepth(path) == 1;
    }

    public String getParentPath() {
        return path.substring(0, path.length() - DEPTH_CHUNK_SIZE);
    }

    public CommentPath createChildCommentPath(String descendentsTopPath) {
        if (descendentsTopPath == null) {
            return CommentPath.create(path + MIN_CHUNK);
        }
        String childrenTopPath = findChildrenTopPath(descendentsTopPath);
        return CommentPath.create(increase(childrenTopPath));
    }

    private String findChildrenTopPath(String descendentsTopPath) {
        return descendentsTopPath.substring(0, (getDepth()+1)*DEPTH_CHUNK_SIZE);
    }

    private String increase(String path) {
        String lastChunk = path.substring(path.length() - DEPTH_CHUNK_SIZE);
        if (isChunkOverflowed(lastChunk)) {
            throw new IllegalArgumentException("Chunk Overflowed");
        }

        int charsetLength = CHARSET.length();
        int value=0;
        for(char ch : lastChunk.toCharArray()) {
            value = value * charsetLength + CHARSET.indexOf(ch);
        }

        value += 1;

        String result="";
        for(int i=0; i<DEPTH_CHUNK_SIZE; i++) {
            result = CHARSET.charAt(value % charsetLength) + result;
            value /= charsetLength;
        }

        return path.substring(0, path.length() - DEPTH_CHUNK_SIZE) + result;
        //현재댓글의 상속정보 + last댓글+1
    }

    private boolean isChunkOverflowed(String lastChunk) {
        return MAX_CHUNK.equals(lastChunk);
    }
}
