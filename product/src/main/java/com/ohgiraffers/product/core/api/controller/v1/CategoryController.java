package com.ohgiraffers.product.core.api.controller.v1;

import com.ohgiraffers.product.core.api.controller.v1.request.CategoryRequest;
import com.ohgiraffers.product.core.api.controller.v1.response.CategoryResponse;
import com.ohgiraffers.product.core.domain.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    // 카테고리 등록
    @PostMapping
    public CategoryResponse createCategory(
            @AuthenticationPrincipal String adminId,
            @RequestBody CategoryRequest request
    ) {
        return categoryService.createCategory(request);
    }

    // 카테고리 수정
    @PutMapping("/{id}")
    public CategoryResponse updateCategory(@AuthenticationPrincipal String adminId, @PathVariable Long id, @RequestBody CategoryRequest request) {
        return categoryService.updateCategory(id, request);
    }

    // 카테고리 삭제
    @DeleteMapping("/{id}")
    public void deleteCategory(@AuthenticationPrincipal String adminId, @PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}