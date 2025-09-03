package edu.unialfa.alberguepro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}



@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .authorizeHttpRequests((requests) -> requests
                    .requestMatchers("/webjars/**", "/login").permitAll()
                    .requestMatchers("/admin/**").hasRole("ADMIN") // Apenas usuários com role ADMIN podem acessar /admin/...
                    .anyRequest().authenticated()
            )
            .formLogin((form) -> form
                    .loginPage("/login")
                    .defaultSuccessUrl("/", true)
                    .permitAll()
            )
            .logout((logout) -> logout
                .logoutSuccessUrl("/login?logout") // Redireciona para a pág. de login com msg
                .permitAll()
            );

    return http.build();
}

@Bean
public UserDetailsService userDetailsService() {
    // Busca o nosso bean de PasswordEncoder
    PasswordEncoder encoder = passwordEncoder();

    // Cria um usuário comum com senha criptografada
    UserDetails user =
            User.withUsername("user")
                    .password(encoder.encode("password"))
                    .roles("USER")
                    .build();

    // Cria um usuário administrador com senha criptografada
    UserDetails admin =
            User.withUsername("admin")
                    .password(encoder.encode("admin123"))
                    .roles("ADMIN")
                    .build();

    return new InMemoryUserDetailsManager(user, admin);
}
}