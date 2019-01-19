package com.madadipouya.cisapify;

import com.madadipouya.cisapify.user.model.Role;
import com.madadipouya.cisapify.user.model.User;
import com.madadipouya.cisapify.user.repository.RoleRepository;
import com.madadipouya.cisapify.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

import java.util.Set;

@SpringBootApplication
@ComponentScan("com.madadipouya.cisapify")
@EnableJpaRepositories("com.madadipouya.cisapify")
@EntityScan("com.madadipouya.cisapify")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Component
    public class DataLoader implements ApplicationRunner {

        private UserService userService;

        private RoleRepository roleRepository;

        @Autowired
        public DataLoader(UserService userService, RoleRepository roleRepository) {
            this.userService = userService;
            this.roleRepository = roleRepository;
        }

        public void run(ApplicationArguments args) {
            Role adminRole = new Role();
            adminRole.setRole("ADMIN");

            Role userRole = new Role();
            userRole.setRole("USER");

            User user = new User();
            user.setPassword("12345");
            user.setEmailAddress("kasra@madadipouya.com");
            user.setSongs(Set.of());
            user.setEnabled(true);
            user.setRoles(Set.of(adminRole));
            userService.save(user);

            User testUser = new User();
            testUser.setPassword("password");
            testUser.setEmailAddress("test@test.com");
            testUser.setEnabled(true);
            testUser.setSongs(Set.of());
            testUser.setRoles(Set.of(userRole));
            userService.save(testUser);
        }
    }
}