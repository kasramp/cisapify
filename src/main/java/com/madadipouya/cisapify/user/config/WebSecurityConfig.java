package com.madadipouya.cisapify.user.config;

import com.madadipouya.cisapify.user.filter.JwtAuthenticationFilter;
import com.madadipouya.cisapify.user.filter.JwtAuthorizationFilter;
import com.madadipouya.cisapify.user.model.Role;
import com.madadipouya.cisapify.user.repository.UserRepository;
import com.madadipouya.cisapify.user.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserRepository userRepository;

    public WebSecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Configuration
    @Order(1)
    public static class JWTConfigurerAdapter extends WebSecurityConfigurerAdapter {

        private final JwtService jwtService;

        public JWTConfigurerAdapter(JwtService jwtService) {
            this.jwtService = jwtService;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/v1/**")
                    .cors().and().csrf().disable()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/api/v1/authenticate").permitAll()
                    .antMatchers("/api/v1/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
                    .and()
                    .addFilter(new JwtAuthenticationFilter(jwtService))
                    .addFilter(new JwtAuthorizationFilter(authenticationManager(), jwtService))
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }
    }

    @Configuration
    @Order(2)
    public static class BasicAuthConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .antMatchers("/user/**", "/files/**").hasAnyRole("ADMIN", "USER")
                    .and()
                    .formLogin().loginPage("/login")
                    .defaultSuccessUrl("/welcome")
                    .and()
                    .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login?logout")
                    .clearAuthentication(true)
                    .deleteCookies("JSESSIONID")
                    .invalidateHttpSession(true);
        }

        @Override
        @Bean
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return emailAddress -> {
            com.madadipouya.cisapify.user.model.User user = userRepository.getByEmailAddress(emailAddress)
                    .orElseThrow(() -> new UsernameNotFoundException("could not find the user"));
            return User.withUsername(user.getEmailAddress())
                    .password(user.getPassword())
                    .disabled(!user.isEnabled())
                    .accountExpired(false)
                    .credentialsExpired(false)
                    .accountLocked(false)
                    .roles(user.getRoles().stream().map(Role::getRole).toArray(String[]::new))
                    .build();
        };
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}