package com.mily.article.milyx.category;

import com.mily.article.milyx.category.entity.FirstCategory;
import com.mily.article.milyx.category.entity.SecondCategory;
import com.mily.article.milyx.category.repository.FirstCategoryRepository;
import com.mily.article.milyx.category.repository.SecondCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final FirstCategoryRepository fcr;
    private final SecondCategoryRepository scr;

    public List<FirstCategory> getFirstCategories() {
        return fcr.findAll();
    }

    public List<SecondCategory> getSecondCategories() {
        return scr.findAll();
    }

    public List<SecondCategory> findSecondCategoriesByFirstCategory(String firstCategory) {
        return scr.findByFirstCategory_Title(firstCategory);
    }

    public FirstCategory addFC(String title) {
        FirstCategory fc = FirstCategory.builder()
                .title(title)
                .build();

        return fcr.save(fc);
    }

    public SecondCategory addSC(String title, FirstCategory firstCategory) {
        SecondCategory sc = SecondCategory.builder()
                .title(title)
                .firstCategory(firstCategory)
                .build();

        return scr.save(sc);
    }

    public FirstCategory findByFTitle(String title) {
        return fcr.findByTitle(title)
                .orElseThrow(() -> new NoSuchElementException("1차 카테고리를 정확히 입력해주세요 : " + title));
    }

    public SecondCategory findBySTitle(String title) {
        return scr.findByTitle(title)
                .orElseThrow(() -> new NoSuchElementException("1차 카테고리를 정확히 입력해주세요 : " + title));
    }

    public FirstCategory findByFId(Integer id) {
        return fcr.findById(id)
                .orElseThrow(() -> new NoSuchElementException("다음에 대한 검색 결과가 없습니다. : " + id));
    }

    public SecondCategory findBySId(Integer id) {
        return scr.findById(id)
                .orElseThrow(() -> new NoSuchElementException("다음에 대한 검색 결과가 없습니다. : " + id));
    }

    public List<SecondCategory> findByFirstCategoryId(int firstCategoryId) {
        return scr.findByFirstCategoryId(firstCategoryId);
    }
}