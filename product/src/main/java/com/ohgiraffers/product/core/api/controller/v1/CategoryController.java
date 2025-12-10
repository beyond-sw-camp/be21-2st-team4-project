package com.ohgiraffers.product.core.api.controller.v1;

import com.ohgiraffers.product.core.api.controller.v1.request.CategoryRequest;
import com.ohgiraffers.product.core.api.controller.v1.response.CategoryResponse;
import com.ohgiraffers.product.core.domain.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "카테고리 API", description = "상품 카테고리 등록, 수정, 삭제 기능을 제공합니다.")
public class CategoryController {
    private final CategoryService categoryService;

    // 카테고리 등록
    @PostMapping("/category")
    @Operation(summary = "카테고리 등록", description = "새로운 카테고리를 등록합니다.")
    @ApiResponse(responseCode = "201", description = "카테고리 등록 성공",
            content = @Content(schema = @Schema(implementation = CategoryResponse.class)))
    public CategoryResponse createCategory(
            @AuthenticationPrincipal String adminId,
            @RequestBody CategoryRequest request
    ) {
        return categoryService.createCategory(request);
    }

    // 카테고리 수정
    @PutMapping("/category/{id}")
    @Operation(summary = "카테고리 수정", description = "카테고리 ID를 이용해 기존 카테고리를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "카테고리 수정 성공",
            content = @Content(schema = @Schema(implementation = CategoryResponse.class)))
    @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음")
    public CategoryResponse updateCategory(@AuthenticationPrincipal String adminId, @PathVariable Long id, @RequestBody CategoryRequest request) {
        return categoryService.updateCategory(id, request);
    }

    // 카테고리 삭제
    @DeleteMapping("/category/{id}")
    @Operation(summary = "카테고리 삭제", description = "카테고리 ID를 이용해 특정 카테고리를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "카테고리 삭제 성공")
    @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음")
    public void deleteCategory(@AuthenticationPrincipal String adminId, @PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}