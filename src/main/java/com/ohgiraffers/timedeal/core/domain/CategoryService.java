package com.ohgiraffers.timedeal.core.domain;

import com.ohgiraffers.timedeal.core.api.controller.v1.request.CategoryRequest;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.CategoryResponse;
import com.ohgiraffers.timedeal.storage.CategoryRepository;
import com.ohgiraffers.timedeal.core.enums.EntityStatus;
import com.ohgiraffers.timedeal.core.support.error.CoreException;
import com.ohgiraffers.timedeal.core.support.error.ErrorType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // 카테고리 등록
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = new Category(request.getName());

        // 상태 설정
        if ("ACTIVE".equalsIgnoreCase(request.getStatus())) {
            category.active();
        } else if ("DELETED".equalsIgnoreCase(request.getStatus())) {
            category.deleted();
        }

        Category saved = categoryRepository.save(category);
        return CategoryResponse.from(saved);
    }


    // 카테고리 수정
    @Transactional
    public CategoryResponse updateCategory(Long categoryId, CategoryRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        category.update(request.getName());
        // 상태 변경
        if ("ACTIVE".equalsIgnoreCase(request.getStatus())) {
            category.active();
        } else if ("DELETED".equalsIgnoreCase(request.getStatus())) {
            category.deleted();
        }

        Category updated = categoryRepository.save(category);
        return CategoryResponse.from(updated);
    }

    // 카테고리 삭제 (실제 삭제 대신 상태 변경)
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        category.deleted(); // BaseEntity 메서드 활용
        categoryRepository.save(category);
    }

    // 전체 조회
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::from)
                .toList();
    }

    // 단건 조회
    @Transactional(readOnly = true)
    public CategoryResponse findById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));
        return CategoryResponse.from(category);
    }
}