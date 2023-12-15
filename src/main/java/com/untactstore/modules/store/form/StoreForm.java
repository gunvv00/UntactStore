package com.untactstore.modules.store.form;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class StoreForm {
    public static final String VALID_STORENAME_PATTERN = "^[ㄱ-ㅎ가-힣a-z0-9_-]{2,20}$";

    @NotBlank
    private String licensee;

    @NotBlank
    @Length(min = 2, max = 20)
    @Pattern(regexp = VALID_STORENAME_PATTERN)
    private String path;

    @NotBlank
    @Length(max = 50)
    private String name;

    private String phone;

    @NotBlank
    @Length(max = 100)
    private String shortDescription;

    @NotBlank
    private String fullDescription;

}
