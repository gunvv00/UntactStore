package com.untactstore.modules.account.form;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class Profile {
    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;
}
