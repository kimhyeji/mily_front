package com.mily.article.milyx.category.repository;

import com.mily.article.milyx.category.entity.FirstCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FirstCategoryRepository extends JpaRepository <FirstCategory, Integer> {
    List<FirstCategory> findAll();
    Optional<FirstCategory> findByTitle(String title);
}