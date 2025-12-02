package com.ohgiraffers.timedeal.core.domain;

import com.ohgiraffers.timedeal.core.api.controller.v1.request.AdminRequest;
import com.ohgiraffers.timedeal.core.api.controller.v1.response.AdminResponse;
import com.ohgiraffers.timedeal.storage.AdminRepository;
import com.ohgiraffers.timedeal.core.support.error.CoreException;
import com.ohgiraffers.timedeal.core.support.error.ErrorType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    // 관리자 등록
    @Transactional
    public AdminResponse createAdmin(AdminRequest request) {
        Admin admin = new Admin(
                request.getEmail(),
                request.getPassword(),
                request.getCompany()
        );
        Admin saved = adminRepository.save(admin);
        return AdminResponse.from(saved);
    }

    // 관리자 단건 조회
    @Transactional(readOnly = true)
    public AdminResponse findById(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));
        return AdminResponse.from(admin);
    }

    // 관리자 전체 조회
    @Transactional(readOnly = true)
    public List<AdminResponse> findAll() {
        return adminRepository.findAll().stream()
                .map(AdminResponse::from)
                .collect(Collectors.toList());
    }

    // 관리자 삭제
    @Transactional
    public void deleteAdmin(Long adminId) {
        // FIX: 불필요한 조회 없이 deleteById 사용
        adminRepository.deleteById(adminId);
    }
}