package com.mily.article.milyx.comment;

import com.mily.article.milyx.MilyX;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MilyXCommentRepository extends JpaRepository<MilyXComment, Long> {
    List<MilyXComment> findByMilyX (MilyX milyX);

    List<MilyXComment> findByAuthorId(long id);
}