package com.example.mealplanner.service;

import com.example.mealplanner.dto.LoginRequest;
import com.example.mealplanner.dto.RegisterRequest;
import com.example.mealplanner.entity.User;
import com.example.mealplanner.repository.UserRepository;
import com.example.mealplanner.security.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("このメールアドレスは既に使用されています");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setRole("USER");

        userRepository.save(user);
    }

    public void login(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("メールアドレスまたはパスワードが正しくありません"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("メールアドレスまたはパスワードが正しくありません");
        }

        String token = jwtProvider.generateToken(user.getEmail(), user.getRole(), user.getId());
        addTokenCookie(response, token, 86400);
    }

    public void logout(HttpServletResponse response) {
        addTokenCookie(response, "", 0);
    }

    private void addTokenCookie(HttpServletResponse response, String value, int maxAge) {
        response.addHeader("Set-Cookie",
            String.format("token=%s; HttpOnly; Secure; SameSite=Lax; Path=/; Max-Age=%d", value, maxAge));
    }

    public static class EmailAlreadyExistsException extends RuntimeException {
        public EmailAlreadyExistsException(String message) {
            super(message);
        }
    }
}
