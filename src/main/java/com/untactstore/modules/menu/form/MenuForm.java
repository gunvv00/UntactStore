package com.untactstore.modules.menu.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MenuForm {
    @NotBlank
    private String name;

    private Integer price;

    private String image = null;

    private String explanation;
}
