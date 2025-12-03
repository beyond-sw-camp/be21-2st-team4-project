package com.ohgiraffers.timedeal.core.domain;

import com.ohgiraffers.timedeal.core.api.controller.v1.request.ProductRequest;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.ProductListResponse;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.ProductResponse;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.AdminResponse;
import com.ohgiraffers.timedeal.storage.AdminRepository;
import com.ohgiraffers.timedeal.storage.CategoryRepository;
import com.ohgiraffers.timedeal.storage.ProductRepository;
import com.ohgiraffers.timedeal.core.support.error.CoreException;
import com.ohgiraffers.timedeal.core.support.error.ErrorType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final AdminRepository adminRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
                          AdminRepository adminRepository,
                          CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.adminRepository = adminRepository;
        this.categoryRepository = categoryRepository;
    }

    // 상품 등록
    @Transactional
    public void createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        Admin admin = adminRepository.findById(request.getAdminId())
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        Product product = new Product(
                request.getName(),
                request.getDescription(),
                request.getImageUrl(),
                request.getPrice(),
                category,
                admin
        );

        productRepository.save(product);
    }

    // 상품 수정
    @Transactional
    public void update(Long productId, ProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        Admin admin = adminRepository.findById(request.getAdminId())
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        product.update(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getImageUrl(),
                category,
                admin
        );

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
        List<Product> products = productRepository.findAllWithCategory();
        return ProductListResponse.from(products);
    }

    // 단건 조회
    @Transactional(readOnly = true)
    public ProductResponse findById(Long productId) {
        Product product = productRepository.findByIdWithCategory(productId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));
        return ProductResponse.from(product);
    }

    // 관리자별 조회
    @Transactional(readOnly = true)
    public ProductListResponse findByAdminId(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        List<Product> products = productRepository.findByAdminIdWithCategory(admin.getId());
        return ProductListResponse.from(products);
    }

    // 관리자 권한으로 상품 수정
    @Transactional
    public ProductResponse updateProductByAdmin(Long adminId, Long productId, ProductRequest request) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        if (!product.getAdmin().getId().equals(admin.getId())) {
            throw new CoreException(ErrorType.DEFAULT_ARGUMENT_NOT_VALID);
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        product.update(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getImageUrl(),
                category,
                admin
        );

        return ProductResponse.from(productRepository.save(product));
    }

    // 관리자 권한으로 상품 삭제
    @Transactional
    public void deleteProductByAdmin(Long adminId, Long productId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        if (!product.getAdmin().getId().equals(admin.getId())) {
            throw new CoreException(ErrorType.DEFAULT_ARGUMENT_NOT_VALID);
        }

        productRepository.delete(product);
    }

}