package com.untactstore.modules.review;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ReviewForm {

    @NotBlank
    private String content;

}
