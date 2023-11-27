package com.mily.article.milyx.category;

import com.mily.article.milyx.category.entity.SecondCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/getSecondCategory")
    public ResponseEntity<List<SecondCategory>> getSecondCategory (@RequestParam String firstCategory) {
        List<SecondCategory> secondCategories = categoryService.findSecondCategoriesByFirstCategory(firstCategory);
        return ResponseEntity.ok(secondCategories);
    }
}