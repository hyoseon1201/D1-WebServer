package com.d1.server.service;

import com.d1.server.dto.LoginRequest;
import com.d1.server.dto.LoginResponse;
import com.d1.server.dto.RegisterRequest;
import com.d1.server.entity.Account;
import com.d1.server.exception.ApiException;
import com.d1.server.repository.AccountRepository;
import com.d1.server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (accountRepository.existsByEmail(request.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.");
        }
        Account account = new Account(request.email(), passwordEncoder.encode(request.password()));
        accountRepository.save(account);
        String token = jwtUtil.generateToken(account.getAccountId());
        return new LoginResponse(token, account.getAccountId());
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Account account = accountRepository.findByEmail(request.email())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."));
        if (!passwordEncoder.matches(request.password(), account.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        String token = jwtUtil.generateToken(account.getAccountId());
        return new LoginResponse(token, account.getAccountId());
    }
}
