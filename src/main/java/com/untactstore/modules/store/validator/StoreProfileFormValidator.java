package com.untactstore.modules.store.validator;

import com.untactstore.modules.store.form.StoreProfileForm;
import com.untactstore.modules.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class StoreProfileFormValidator implements Validator {
    private final StoreRepository storeRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(StoreProfileForm.class);
    }

    @Override
    public void validate(Object object, Errors errors) {
        StoreProfileForm storeProfileForm = (StoreProfileForm)object;

//        if (storeRepository.existsByPath(storeProfileForm.getPath())) {
//            errors.rejectValue("path", "invalid.path",
//                    new Object[]{storeProfileForm.getPath()}, "이미 사용중인 path 입니다.");
//        }
    }
}
