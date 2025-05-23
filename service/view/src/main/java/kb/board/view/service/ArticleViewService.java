package kb.board.view.service;

import kb.board.view.repository.ArticleViewCountRepository;
import kb.board.view.repository.ArticleViewDistributedLockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ArticleViewService {
    private final ArticleViewCountRepository articleViewCountRepository;
    private final ArticleViewCountBackUpProcessor articleViewCountBackUpProcessor;
    private final ArticleViewDistributedLockRepository articleViewDistributedLockRepository;

    private static final int BACKUP_BACH_SIZE = 100;
    private static final Duration TTL = Duration.ofMinutes(10);

    public Long increase(Long articleId, Long userId) {
        if(!articleViewDistributedLockRepository.lock(articleId, userId, TTL)){
            return articleViewCountRepository.read(articleId);
        }


        Long count = articleViewCountRepository.increase(articleId);
        if(count%BACKUP_BACH_SIZE == 0){
            articleViewCountBackUpProcessor.backUp(articleId, count);
        }
        return count;
    }

    public Long count(Long articleId) {
        return articleViewCountRepository.read(articleId);
    }
}
