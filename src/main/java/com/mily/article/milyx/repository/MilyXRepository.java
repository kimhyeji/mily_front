package com.mily.article.milyx.repository;

import com.mily.article.milyx.MilyX;
import com.mily.user.MilyUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MilyXRepository extends JpaRepository<MilyX, Long> {
    List<MilyX> findByAuthor(MilyUser author);
}