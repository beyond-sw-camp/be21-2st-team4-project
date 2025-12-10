package com.ohgiraffers.product.core.domain;

import com.ohgiraffers.common.support.error.CoreException;
import com.ohgiraffers.common.support.error.ErrorType;
import com.ohgiraffers.product.core.api.command.CommandClient;
import com.ohgiraffers.product.core.api.controller.v1.request.ProductRequest;
import com.ohgiraffers.product.core.api.controller.v1.response.ProductResponse;
import com.ohgiraffers.product.storage.CategoryRepository;
import com.ohgiraffers.product.storage.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CommandClient commandClient;

    // 상품 등록
    @Transactional
    public void createProduct(Long adminId, ProductRequest request) {
        if (!categoryRepository.existsById(request.categoryId())) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }

        Product product = new Product(
                adminId,
                request.categoryId(),
                request.name(),
                request.description(),
                request.imageUrl(),
                request.price()
        );

        productRepository.save(product);
    }

    // 상품 수정
    @Transactional
    public void update(Long adminId, Long productId, ProductRequest request) {
        if (!categoryRepository.existsById(request.categoryId())) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        if(!adminId.equals(product.getAdminId())) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }

        product.update(
                request.categoryId(),
                request.name(),
                request.description(),
                request.price(),
                request.imageUrl()
        );

        productRepository.save(product);
    }

    // 상품 삭제
    @Transactional
    public void delete(Long adminId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        if(!product.getAdminId().equals(adminId)) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }

        productRepository.delete(product);
    }

    // 단건 조회
    @Transactional(readOnly = true)
    public ProductResponse findById(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        Category category = categoryRepository.findById(product.getCategoryId())
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        String company = commandClient.getAdminCompany(product.getAdminId());

        return new ProductResponse(
                productId,
                company,
                category.getName(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImageUrl()
        );
    }
}