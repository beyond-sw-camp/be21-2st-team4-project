package com.ohgiraffers.timedeal.core.api.controller.v1;

import com.ohgiraffers.timedeal.core.api.controller.v1.request.ProductRequest;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.ProductListResponse;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.ProductResponse;
import com.ohgiraffers.timedeal.core.domain.ProductService;
import com.ohgiraffers.timedeal.core.support.response.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    // 1. 상품 등록
    @PostMapping("/api/v1/products")
    public ApiResult<?> createProduct(@RequestBody ProductRequest request) {
        productService.createProduct(request);
        return ApiResult.success();
    }

    // 2. 상품 수정
    @PutMapping("/api/v1/products/{productid}")
    public ApiResult<?> update(@PathVariable Long productid, @RequestBody ProductRequest request) {
        productService.update(productid, request);
        return ApiResult.success();
    }

    // 3. 상품 삭제
    @DeleteMapping("/api/v1/products/{productid}")
    public ApiResult<?> delete(@PathVariable Long productid) {
        productService.delete(productid);
        return ApiResult.success();
    }

    // 4. 전체 조회
    @GetMapping("/api/v1/products")
    public ApiResult<ProductListResponse> findAll() {
        // productService.findAll()이 ProductListResponse 객체를 반환하므로 타입 일치
        return ApiResult.success(productService.findAll());
    }

    // 5. 단건 조회
    @GetMapping("/api/v1/products/{productid}")
    public ApiResult<ProductResponse> findById(@PathVariable Long productid) {
        return ApiResult.success(productService.findById(productid));
    }
}