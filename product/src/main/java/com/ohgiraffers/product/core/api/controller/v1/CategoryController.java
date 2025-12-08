package com.ohgiraffers.product.core.api.controller.v1;

import com.ohgiraffers.product.core.api.controller.v1.request.CategoryRequest;
import com.ohgiraffers.product.core.api.controller.v1.response.CategoryResponse;
import com.ohgiraffers.product.core.domain.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // 카테고리 등록
    @PostMapping
    public CategoryResponse createCategory(@RequestBody CategoryRequest request) {
        return categoryService.createCategory(request);
    }

    // 카테고리 수정
    @PutMapping("/{id}")
    public CategoryResponse updateCategory(@PathVariable Long id, @RequestBody CategoryRequest request) {
        return categoryService.updateCategory(id, request);
    }

    // 카테고리 삭제
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }

    // 전체 조회
    @GetMapping
    public List<CategoryResponse> findAll() {
        return categoryService.findAll();
    }

    // 단건 조회
    @GetMapping("/{id}")
    public CategoryResponse findById(@PathVariable Long id) {
        return categoryService.findById(id);
    }
}