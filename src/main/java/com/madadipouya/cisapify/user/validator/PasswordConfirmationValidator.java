package com.madadipouya.cisapify.user.validator;

import com.madadipouya.cisapify.user.controller.ProfileController;
import com.madadipouya.cisapify.user.metadata.ConfirmPassword;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordConfirmationValidator implements ConstraintValidator<ConfirmPassword, ProfileController.UserCommand> {

    @Override
    public boolean isValid(ProfileController.UserCommand value, ConstraintValidatorContext context) {
        return StringUtils.equals(value.getPassword(), value.getConfirmPassword());
    }
}