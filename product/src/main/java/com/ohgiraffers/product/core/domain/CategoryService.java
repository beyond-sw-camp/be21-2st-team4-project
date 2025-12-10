package com.ohgiraffers.product.core.domain;

import com.ohgiraffers.common.support.error.CoreException;
import com.ohgiraffers.common.support.error.ErrorType;
import com.ohgiraffers.product.core.api.command.CommandClient;
import com.ohgiraffers.product.core.api.controller.v1.request.CategoryRequest;
import com.ohgiraffers.product.core.api.controller.v1.response.CategoryResponse;
import com.ohgiraffers.product.storage.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CommandClient commandClient;

    // 카테고리 등록
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Category saved = categoryRepository.save(new Category(request.name()));
        return CategoryResponse.from(saved);
    }

    // 카테고리 수정
    @Transactional
    public CategoryResponse updateCategory(Long categoryId, CategoryRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        category.update(request.name());

        Category updated = categoryRepository.save(category);
        return CategoryResponse.from(updated);
    }

    // 카테고리 삭제 (상태 변경)
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        category.deleted();
        categoryRepository.save(category);
    }
}