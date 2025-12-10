package com.ohgiraffers.account.core.domain;

import com.ohgiraffers.account.core.api.controller.v1.request.AdminRequest;
import com.ohgiraffers.account.core.api.controller.v1.response.AdminResponse;
import com.ohgiraffers.account.core.api.controller.v1.response.AdminSignInResponse;
import com.ohgiraffers.account.core.api.controller.v1.response.SignInResponse;
import com.ohgiraffers.account.security.JwtTokenProvider;
import com.ohgiraffers.account.storage.AdminRepository;
import com.ohgiraffers.common.support.error.CoreException;
import com.ohgiraffers.common.support.error.ErrorType;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    // 관리자 등록
    @Transactional
    public void createAdmin(String email, String password, String company) {
        // 1. 이메일 중복 검사
        if (adminRepository.existsByEmail(email)) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }
        String encodedPassword = passwordEncoder.encode(password);
        Admin admin = new Admin(email, encodedPassword, company);
        adminRepository.save(admin);
    }

    public AdminSignInResponse adminSignIn(String email, String password) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new CoreException(ErrorType.DEFAULT_ERROR);
        }

        String accessToken = jwtTokenProvider.createToken(
                admin.getEmail(),
                "ADMIN",
                admin.getId()
        );

        return new AdminSignInResponse(admin.getId(), accessToken);
    }

    public AdminResponse getAdmin(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));
        return new AdminResponse(admin.getId(), admin.getEmail(), admin.getCompany());
    }
}