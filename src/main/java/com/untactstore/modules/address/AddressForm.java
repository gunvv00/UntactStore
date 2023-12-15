package com.untactstore.modules.address;

import lombok.Data;

@Data
public class AddressForm {
    private String postcode;
    private String roadAddress;
    private String jibunAddress;
    private String detailAddress;
    private String extraAddress;
}
