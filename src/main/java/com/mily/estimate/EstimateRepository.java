package com.mily.estimate;

import com.mily.article.milyx.category.entity.FirstCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EstimateRepository extends JpaRepository<Estimate, Long> {
    List<Estimate> findByCreateDateGreaterThanEqualAndArea(LocalDateTime createDate, String area);
    List<Estimate> findByCreateDateGreaterThanEqualAndAreaAndFirstCategory(LocalDateTime createDate, String area, FirstCategory firstCategory);
    List<Estimate> findAll();
    void deleteByCreateDateLessThan(LocalDateTime createDate);
}