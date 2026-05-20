package com.seleebe.youowe.auth;

import com.seleebe.youowe.security.CustomUserDetailsService;
import com.seleebe.youowe.security.JwtService;
import com.seleebe.youowe.user.CreateUserDto;
import com.seleebe.youowe.user.UserResponseDto;
import com.seleebe.youowe.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;
  private final AuthenticationManager authenticationManager;
  private final CustomUserDetailsService userDetailsService;
  private final JwtService jwtService;

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public UserResponseDto register(@Valid @RequestBody CreateUserDto dto) {
    return userService.createUser(dto);
  }

  @PostMapping("/login")
  public JwtResponse login(@RequestBody JwtRequest authRequest) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            authRequest.getUsername(),
            authRequest.getPassword()
        )
    );
    UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
    String token = jwtService.generateToken(userDetails);

    return new JwtResponse(token);
  }
}
