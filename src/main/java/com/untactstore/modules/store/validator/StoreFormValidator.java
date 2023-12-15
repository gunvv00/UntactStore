package com.untactstore.modules.store.validator;

import com.untactstore.modules.store.form.StoreForm;
import com.untactstore.modules.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class StoreFormValidator implements Validator {
    private final StoreRepository storeRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(StoreForm.class);
    }

    @Override
    public void validate(Object object, Errors errors) {
        StoreForm storeForm = (StoreForm)object;

        if (storeRepository.existsByPath(storeForm.getPath())) {
            errors.rejectValue("path", "invalid.path",
                    new Object[]{storeForm.getPath()}, "이미 사용중인 path 입니다.");
        }
    }
}
