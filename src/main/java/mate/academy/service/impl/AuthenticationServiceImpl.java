package mate.academy.service.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.dto.user.UserLoginRequestDto;
import mate.academy.exception.AuthenticationException;
import mate.academy.repository.UserRepository;
import mate.academy.security.JwtUtil;
import mate.academy.service.AuthenticationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public String login(UserLoginRequestDto requestDto) {
        var user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid email or password."));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid email or password.");
        }

        return jwtUtil.generateToken(user.getEmail());
    }
}
