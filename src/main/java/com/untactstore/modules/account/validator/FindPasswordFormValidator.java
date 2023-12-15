package com.untactstore.modules.account.validator;

import com.untactstore.modules.account.form.FindPasswordForm;
import com.untactstore.modules.account.form.SignUpForm;
import com.untactstore.modules.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class FindPasswordFormValidator implements Validator {
    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(FindPasswordForm.class);
    }

    @Override
    public void validate(Object object, Errors errors) {
        FindPasswordForm findPasswordForm = (FindPasswordForm)object;

//        //username 중복 확인
//        if (accountRepository.existsByUsername(findPasswordForm.getUsername())) {
//            errors.rejectValue("username", "invalid.username",
//                    new Object[]{findPasswordForm.getUsername()}, "이미 사용중인 username 입니다.");
//        }
//        //email 중복 확인
//        if (accountRepository.existsByEmail(signUpForm.getEmail())) {
//            errors.rejectValue("email", "invalid.email",
//                    new Object[]{signUpForm.getEmail()}, "이미 사용중인 email 입니다.");
//        }
//        TODO 아이디에 맞는 이메일인디 확인

    }
}
