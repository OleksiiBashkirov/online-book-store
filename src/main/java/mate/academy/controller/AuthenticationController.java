package mate.academy.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.UserLoginRequestDto;
import mate.academy.dto.UserLoginResponseDto;
import mate.academy.dto.UserRegistrationRequestDto;
import mate.academy.dto.UserResponseDto;
import mate.academy.exception.RegistrationException;
import mate.academy.service.AuthenticationService;
import mate.academy.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }

    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.login(requestDto);
    }
}
