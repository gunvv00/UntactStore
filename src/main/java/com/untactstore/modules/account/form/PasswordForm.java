package com.untactstore.modules.account.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class PasswordForm {
    @Length(min = 7, max = 50)
    private String newPassword;

    @Length(min = 7, max = 50)
    private String newPasswordConfirm;
}
