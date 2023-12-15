package com.untactstore.modules.store.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class StoreSettingsForm {
    @NotBlank
    private String licensee;
}
