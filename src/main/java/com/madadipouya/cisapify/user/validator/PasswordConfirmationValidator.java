package com.madadipouya.cisapify.user.validator;

import com.madadipouya.cisapify.user.controller.ProfileController;
import com.madadipouya.cisapify.user.metadata.ConfirmPassword;
import com.madadipouya.cisapify.user.service.UserService;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordConfirmationValidator implements ConstraintValidator<ConfirmPassword, ProfileController.UserCommand> {

    private final UserService userService;

    PasswordConfirmationValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean isValid(ProfileController.UserCommand value, ConstraintValidatorContext context) {
        return StringUtils.equals(value.getPassword(), value.getConfirmPassword()) ||
                StringUtils.equals(value.getPassword(), userService.getCurrentUser().getPassword());
    }
}