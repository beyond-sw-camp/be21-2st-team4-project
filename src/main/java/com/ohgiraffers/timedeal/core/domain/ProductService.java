package com.ohgiraffers.timedeal.core.domain;

import com.ohgiraffers.timedeal.core.api.controller.v1.request.ProductRequest;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.ProductListResponse;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.ProductResponse;
import com.ohgiraffers.timedeal.storage.AdminRepository;
import com.ohgiraffers.timedeal.storage.ProductRepository;
import com.ohgiraffers.timedeal.storage.CategoryRepository; // 💡 CategoryRepository 임포트
import com.ohgiraffers.timedeal.core.support.error.CoreException;
import com.ohgiraffers.timedeal.core.support.error.ErrorType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final AdminRepository adminRepository;
    private final CategoryRepository categoryRepository; // 💡 FIX: CategoryRepository 필드 추가

    // 💡 FIX: 생성자에 CategoryRepository 주입
    public ProductService(ProductRepository productRepository, AdminRepository adminRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.adminRepository = adminRepository;
        this.categoryRepository = categoryRepository;
    }

    // 상품 등록
    @Transactional
    public void createProduct(ProductRequest request) {
        adminRepository.findById(request.getAdminId())
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        // 💡 FIX: Category ID로 Category Entity를 조회
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        Product product = new Product(
                request.getName(),
                request.getDescription(),
                request.getImageUrl(),
                request.getPrice(),
                category, // 💡 FIX: Category 객체 전달
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

        // 💡 FIX: Category ID로 Category Entity를 조회
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        product.update(request.getName(), request.getDescription(),
                request.getPrice(), request.getImageUrl(),
                category, // 💡 FIX: Category 객체 전달
                request.getAdminId());

        productRepository.save(product);
    }

    // 상품 삭제
    @Transactional
    public void delete(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));
        productRepository.delete(product);
    }

    // 전체 조회
    @Transactional(readOnly = true)
    public ProductListResponse findAll() {
        // 💡 FIX: findAllWithCategory 사용
        List<Product> products = productRepository.findAllWithCategory();
        return ProductListResponse.from(products);
    }

    // 단건 조회
    @Transactional(readOnly = true)
    public ProductResponse findById(Long productId) {
        // 💡 FIX: findByIdWithCategory 사용
        Product product = productRepository.findByIdWithCategory(productId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));
        return ProductResponse.from(product);
    }

    // 관리자별 조회 (AdminController에서 사용)
    @Transactional(readOnly = true)
    public ProductListResponse findByAdminId(Long adminId) {
        adminRepository.findById(adminId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        // 💡 FIX: findByAdminIdWithCategory 사용
        List<Product> products = productRepository.findByAdminIdWithCategory(adminId);
        return ProductListResponse.from(products);
    }

    // 관리자 권한으로 수정
    @Transactional
    public ProductResponse updateProductByAdmin(Long adminId, Long productId, ProductRequest request) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        // 💡 FIX: Category ID로 Category Entity를 조회
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        if (!product.getAdminId().equals(admin.getId())) {
            throw new CoreException(ErrorType.DEFAULT_ARGUMENT_NOT_VALID);
        }

        product.update(request.getName(), request.getDescription(), request.getPrice(),
                request.getImageUrl(), category, admin.getId()); // 💡 FIX: Category 객체 전달

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