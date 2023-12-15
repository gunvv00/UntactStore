package com.untactstore.modules.review;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ReplyForm {
    @NotBlank
    private String content;

    private Long review_id;
}
