package mate.academy.config;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import lombok.RequiredArgsConstructor;
import mate.academy.security.CustomUserDetailsService;
import mate.academy.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        return http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(this::configureAuthorization)
                .sessionManagement(
                        s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(customUserDetailsService)
                .build();
    }

    private void configureAuthorization(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>
                    .AuthorizationManagerRequestMatcherRegistry authorize) {
        authorize
                .requestMatchers(
                        antMatcher("/auth/**"),
                        antMatcher("/swagger-ui/**"),
                        antMatcher("/v3/api-docs/**")
                ).permitAll()
                .requestMatchers(antMatcher(HttpMethod.GET, "api/books/**"))
                .hasAnyRole("USER", "ADMIN")
                .requestMatchers(antMatcher(HttpMethod.GET, "/api/categories/**"))
                .hasAnyRole("USER", "ADMIN")
                .requestMatchers(antMatcher("/api/cart/**")).hasRole("USER")
                .requestMatchers(antMatcher(HttpMethod.POST, "/api/books/**"))
                .hasRole("ADMIN")
                .requestMatchers(antMatcher(HttpMethod.PUT, "/api/books/**"))
                .hasRole("ADMIN")
                .requestMatchers(antMatcher(HttpMethod.DELETE, "/api/books/**"))
                .hasRole("ADMIN")
                .requestMatchers(antMatcher(HttpMethod.POST, "/api/categories/**"))
                .hasRole("ADMIN")
                .requestMatchers(antMatcher(HttpMethod.PUT, "/api/categories/**"))
                .hasRole("ADMIN")
                .requestMatchers(antMatcher(HttpMethod.DELETE, "/api/categories/**"))
                .hasRole("ADMIN")
                .anyRequest().authenticated();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
            throws Exception {
        return authConfig.getAuthenticationManager();
    }
}

