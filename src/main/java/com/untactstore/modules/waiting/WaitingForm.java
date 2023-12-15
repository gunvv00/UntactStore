package com.untactstore.modules.waiting;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class WaitingForm {
    @NotBlank
    private Integer personnel;
}
