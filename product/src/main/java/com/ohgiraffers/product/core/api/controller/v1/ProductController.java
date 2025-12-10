package com.ohgiraffers.product.core.api.controller.v1;

import com.ohgiraffers.common.support.response.ApiResult;
import com.ohgiraffers.product.core.api.controller.v1.request.ProductRequest;
import com.ohgiraffers.product.core.api.controller.v1.response.ProductResponse;
import com.ohgiraffers.product.core.domain.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    // 1. 상품 등록
    @PostMapping("/product")
    public ApiResult<?> createProduct(@AuthenticationPrincipal String adminId, @RequestBody ProductRequest request) {
        productService.createProduct(Long.parseLong(adminId), request);
        return ApiResult.success();
    }

    // 2. 상품 수정
    @PutMapping("/product/{productid}")
    public ApiResult<?> update(@AuthenticationPrincipal String adminId,@PathVariable Long productid, @RequestBody ProductRequest request) {
        productService.update(Long.parseLong(adminId), productid, request);
        return ApiResult.success();
    }

    // 3. 상품 삭제
    @DeleteMapping("/product/{productid}")
    public ApiResult<?> delete(@AuthenticationPrincipal String adminId, @PathVariable Long productid) {
        productService.delete(Long.parseLong(adminId), productid);
        return ApiResult.success();
    }

    // 5. 단건 조회
    @GetMapping("/product/{productid}")
    public ApiResult<ProductResponse> findById(@PathVariable Long productid) {
        return ApiResult.success(productService.findById(productid));
    }
}