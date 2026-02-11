package com.aincrad.know_recipes_be.service;

import com.aincrad.know_recipes_be.dto.AuthResponse;
import com.aincrad.know_recipes_be.dto.LoginRequest;
import com.aincrad.know_recipes_be.dto.RegisterRequest;
import com.aincrad.know_recipes_be.exception.BusinessRuleException;
import com.aincrad.know_recipes_be.repository.entity.User;
import com.aincrad.know_recipes_be.repository.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Transactional
    public AuthResponse register(RegisterRequest request) throws BusinessRuleException {

        // Validar se email já existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("Email já cadastrado");
        }

        // Validar se username já existe
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessRuleException("Username já existe");
        }

        // Criar novo usuário
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        // Gerar token
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
            .token(token)
            .type("Bearer")
            .userId(savedUser.getId())
            .username(savedUser.getUsername())
            .email(savedUser.getEmail())
            .build();
    }

    public AuthResponse login(LoginRequest request) throws BusinessRuleException {

        // Autenticar
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        // Buscar usuário
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new BusinessRuleException("Usuário não encontrado"));

        // Gerar token
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
            .token(token)
            .type("Bearer")
            .userId(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .build();
    }
}