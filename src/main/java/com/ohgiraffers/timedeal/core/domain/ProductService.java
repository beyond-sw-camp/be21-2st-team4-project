package com.ohgiraffers.timedeal.core.domain;

import com.ohgiraffers.timedeal.core.api.controller.v1.request.ProductRequest;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.ProductListResponse;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.ProductResponse;
import com.ohgiraffers.timedeal.storage.AdminRepository; // 💡 FIX: Import AdminRepository
import com.ohgiraffers.timedeal.storage.ProductRepository; // 💡 FIX: Import ProductRepository
import com.ohgiraffers.timedeal.core.support.error.CoreException;
import com.ohgiraffers.timedeal.core.support.error.ErrorType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors; // Added necessary utility import

@Service
public class ProductService {

    // 💡 FIX: Declare missing repositories as final fields
    private final ProductRepository productRepository;
    private final AdminRepository adminRepository;

    // 💡 FIX: Add constructor for dependency injection
    public ProductService(ProductRepository productRepository, AdminRepository adminRepository) {
        this.productRepository = productRepository;
        this.adminRepository = adminRepository;
    }

    // 상품 등록
    @Transactional
    public void createProduct(ProductRequest request) {
        // Admin 객체를 조회하는 것은 Admin ID 유효성 검사 목적임
        adminRepository.findById(request.getAdminId())
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        Product product = new Product(
                request.getName(),
                request.getDescription(),
                request.getImageUrl(),
                request.getPrice(),
                request.getCategory(),
                request.getAdminId()
        );

        productRepository.save(product);
    }

    // 상품 수정
    @Transactional
    public void update(Long productId, ProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        adminRepository.findById(request.getAdminId())
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        product.update(request.getName(), request.getDescription(),
                request.getPrice(), request.getImageUrl(),
                request.getCategory(), request.getAdminId());

        productRepository.save(product);
    }

    // 상품 삭제 (delete 메소드는 이미 delete(product)로 수정되어 불필요한 조회 방지)
    @Transactional
    public void delete(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));
        productRepository.delete(product);
    }

    // 전체 조회
    @Transactional(readOnly = true)
    public ProductListResponse findAll() {
        List<Product> products = productRepository.findAll();
        // Use the from method in ProductListResponse
        return ProductListResponse.from(products);
    }

    // 단건 조회
    @Transactional(readOnly = true)
    public ProductResponse findById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));
        return ProductResponse.from(product);
    }

    // 관리자별 조회 (AdminController에서 사용)
    @Transactional(readOnly = true)
    public ProductListResponse findByAdminId(Long adminId) {
        adminRepository.findById(adminId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        List<Product> products = productRepository.findByAdminId(adminId);
        return ProductListResponse.from(products);
    }

    // 관리자 권한으로 수정
    @Transactional
    public ProductResponse updateProductByAdmin(Long adminId, Long productId, ProductRequest request) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        if (!product.getAdminId().equals(admin.getId())) {
            throw new CoreException(ErrorType.DEFAULT_ARGUMENT_NOT_VALID);
        }

        product.update(request.getName(), request.getDescription(), request.getPrice(),
                request.getImageUrl(), request.getCategory(), admin.getId());

        return ProductResponse.from(productRepository.save(product));
    }

    // 관리자 권한으로 삭제
    @Transactional
    public void deleteProductByAdmin(Long adminId, Long productId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        if (!product.getAdminId().equals(admin.getId())) {
            throw new CoreException(ErrorType.DEFAULT_ARGUMENT_NOT_VALID);
        }

        productRepository.delete(product);
    }
}