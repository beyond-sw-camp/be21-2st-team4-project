package com.ohgiraffers.timedeal.core.api.controller.v1;

// ... (imports)
import com.ohgiraffers.timedeal.core.api.controller.v1.request.AdminRequest;
import com.ohgiraffers.timedeal.core.api.controller.v1.request.ProductRequest;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.AdminResponse;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.ProductResponse;
// 💡 FIX: ProductListResponse 임포트 추가
import com.ohgiraffers.timedeal.core.api.controller.v1.response.ProductListResponse;
import com.ohgiraffers.timedeal.core.domain.AdminService;
import com.ohgiraffers.timedeal.core.domain.ProductService;
import com.ohgiraffers.timedeal.core.support.response.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ProductService productService;

    // 1. 관리자 등록
    @PostMapping("/api/v1/admins")
    public ApiResult<AdminResponse> createAdmin(@RequestBody AdminRequest request) {
        return ApiResult.success(adminService.createAdmin(request));
    }

    // 2. 관리자별 상품 조회
    @GetMapping("/api/v1/admins/{adminId}/products")
    // ProductListResponse 타입 사용
    public ApiResult<ProductListResponse> findProductsByAdmin(@PathVariable Long adminId) {
        return ApiResult.success(productService.findByAdminId(adminId));
    }

    // 3. 관리자 권한으로 상품 수정
    @PutMapping("/api/v1/admins/{adminId}/products/{productId}")
    public ApiResult<ProductResponse> updateProductByAdmin(@PathVariable Long adminId,
                                                           @PathVariable Long productId,
                                                           @RequestBody ProductRequest request) {
        return ApiResult.success(productService.updateProductByAdmin(adminId, productId, request));
    }

    // 4. 관리자 권한으로 상품 삭제
    @DeleteMapping("/api/v1/admins/{adminId}/products/{productId}")
    // ApiResult<?> 사용
    public ApiResult<?> deleteProductByAdmin(@PathVariable Long adminId,
                                             @PathVariable Long productId) {
        productService.deleteProductByAdmin(adminId, productId);
        return ApiResult.success();
    }
}