package kb.board.view.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kb.board.view.entity.ArticleViewCount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArticleViewCountBackUpRepositoryTest {
    @Autowired
    ArticleViewCountBackUpRepository articleViewCountBackUpRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    @Transactional
    void updateViewCountTest(){
        ArticleViewCount articleViewCount = articleViewCountBackUpRepository.save(
                ArticleViewCount.init(1L, 0L)
        );
        em.flush();
        em.clear();

        int result1 = articleViewCountBackUpRepository.updateViewCount(1L, 100L);
        int result2 = articleViewCountBackUpRepository.updateViewCount(1L, 300L);
        int result3 = articleViewCountBackUpRepository.updateViewCount(1L, 200L);

        //t
        assertThat(result1).isEqualTo(1);
        assertThat(result2).isEqualTo(1);
        assertThat(result3).isEqualTo(0);

        ArticleViewCount articleViewCount1 = articleViewCountBackUpRepository.findById(1L).get();
        assertThat(articleViewCount1.getViewCount()).isEqualTo(300L);
    }

}