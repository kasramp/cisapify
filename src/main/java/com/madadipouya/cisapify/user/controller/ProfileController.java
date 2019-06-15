package com.madadipouya.cisapify.user.controller;

import com.madadipouya.cisapify.integration.gitlab.GitLabSongIndexer;
import com.madadipouya.cisapify.user.metadata.ConfirmPassword;
import com.madadipouya.cisapify.user.model.User;
import com.madadipouya.cisapify.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Controller
@RequestMapping("/user/profile")
public class ProfileController {

    private final GitLabSongIndexer gitLabSongIndexer;

    private final UserService userService;

    public ProfileController(UserService userService, GitLabSongIndexer gitLabSongIndexer) {
        this.userService = userService;
        this.gitLabSongIndexer = gitLabSongIndexer;
    }

    @GetMapping
    public String viewProfile(Model model) {
        model.addAttribute("command", UserCommand.transform(userService.getCurrentUser()));
        return "user/profile.html";
    }

    @PostMapping
    public ModelAndView editProfile(@Validated @ModelAttribute("command") UserCommand command) {
        User user = userService.getCurrentUser();
        boolean isPasswordUpdated = !StringUtils.equals(user.getPassword(), command.getPassword());
        if (isPasswordUpdated) {
            user.setPassword(command.getPassword());
        }
        user.setEmailAddress(command.getEmailAddress());
        user.setGitlabToken(command.getGitlabToken());
        user.setGitlabRepositoryName(command.getGitlabRepositoryName());
        userService.save(user, isPasswordUpdated);
        if (user.hasValidGitSettings()) {
            gitLabSongIndexer.reindexGitLabSongsAsync(user);
        }
        return new ModelAndView("redirect:/user/profile?success");
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

        private String dropboxToken;

        public UserCommand() {

        }

        private UserCommand(String emailAddress, String password, String confirmPassword,
                            String gitlabToken, String gitlabRepositoryName, String dropboxToken) {
            this.emailAddress = emailAddress;
            this.password = password;
            this.confirmPassword = confirmPassword;
            this.gitlabToken = gitlabToken;
            this.gitlabRepositoryName = gitlabRepositoryName;
            this.dropboxToken = dropboxToken;

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

        public String getDropboxToken() {
            return dropboxToken;
        }

        private static UserCommand transform(User user) {
            return new UserCommand(user.getEmailAddress(),
                    user.getPassword(),
                    user.getPassword(),
                    user.getGitlabToken(),
                    user.getGitlabRepositoryName(),
                    user.getDropboxToken()
            );
        }
    }
}
