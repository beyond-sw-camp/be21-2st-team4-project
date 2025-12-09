package com.ohgiraffers.product.core.api.controller.v1;

import com.ohgiraffers.common.support.response.ApiResult;
import com.ohgiraffers.product.core.api.controller.v1.request.ProductRequest;
import com.ohgiraffers.product.core.api.controller.v1.response.ProductListResponse;
import com.ohgiraffers.product.core.api.controller.v1.response.ProductResponse;
import com.ohgiraffers.product.core.domain.ProductService;
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
        System.out.println("=============>" + productid);
        productService.delete(productid);
        return ApiResult.success();
    }

    // 4. 전체 조회
    @GetMapping("/api/v1/products")
    public ApiResult<ProductListResponse> findAll() {
        return ApiResult.success(productService.findAll());
    }

    // 5. 단건 조회
    @GetMapping("/api/v1/products/{productid}")
    public ApiResult<ProductResponse> findById(@PathVariable Long productid) {
        return ApiResult.success(productService.findById(productid));
    }
}