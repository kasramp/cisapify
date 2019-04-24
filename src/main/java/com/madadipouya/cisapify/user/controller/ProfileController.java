package com.madadipouya.cisapify.user.controller;

import com.madadipouya.cisapify.user.metadata.ConfirmPassword;
import com.madadipouya.cisapify.user.model.User;
import com.madadipouya.cisapify.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Controller
@RequestMapping("/user/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String viewProfile(Model model) {
        model.addAttribute("command", UserCommand.transform(userService.getCurrentUser()));
        return "user/profile.html";
    }

    @PostMapping
    public String editProfile(@Validated @ModelAttribute("command") UserCommand command) {
        return "";
    }


    @ConfirmPassword
    public static class UserCommand {

        @NotBlank
        @Email
        private String emailAddress;

        @NotBlank
        private String password;

        @NotBlank
        private String confirmPassword;

        private String gitlabToken;

        private String gitlabRepositoryName;

        public UserCommand() {

        }

        private UserCommand(String emailAddress, String password, String confirmPassword,
                            String gitlabToken, String gitlabRepositoryName) {
            this.emailAddress = emailAddress;
            this.password = password;
            this.confirmPassword = confirmPassword;
            this.gitlabToken = gitlabToken;
            this.gitlabRepositoryName = gitlabRepositoryName;

        }

        private static UserCommand transform(User user) {
            return new UserCommand(
                    user.getEmailAddress(),
                    user.getPassword(),
                    user.getPassword(),
                    user.getGitlabToken(),
                    user.getGitlabRepositoryName()
            );
        }

        public String getEmailAddress() {
            return emailAddress;
        }

        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }

        public String getGitlabToken() {
            return gitlabToken;
        }

        public void setGitlabToken(String gitlabToken) {
            this.gitlabToken = gitlabToken;
        }

        public String getGitlabRepositoryName() {
            return gitlabRepositoryName;
        }

        public void setGitlabRepositoryName(String gitlabRepositoryName) {
            this.gitlabRepositoryName = gitlabRepositoryName;
        }
    }
}
