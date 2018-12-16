package com.madadipouya.cisapify.user.config;

import com.madadipouya.cisapify.user.model.Role;
import com.madadipouya.cisapify.user.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserRepository userRepository;

    public WebSecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin/**").hasAuthority("ADMIN")
                .antMatchers("/player/**", "/upload/**", "/player_old/**", "/play/**", "/files/**",
                        "/playlist/**").hasAnyAuthority("ADMIN", "USER")
                .and()
                .formLogin();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return emailAddress -> {
            com.madadipouya.cisapify.user.model.User user = userRepository.getByEmailAddress(emailAddress)
                    .orElseThrow(() -> new UsernameNotFoundException("could not find the user"));

            return new User(user.getEmailAddress(), user.getPassword(), user.isEnabled(), true, true, true,
                    AuthorityUtils.createAuthorityList(user.getRoles().stream().map(Role::getRole).toArray(String[]::new)));
        };
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}