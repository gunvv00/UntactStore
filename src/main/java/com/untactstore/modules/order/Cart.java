package com.untactstore.modules.order;

import lombok.Data;

@Data
public class Cart {
    private String name = "장바구니";
    private Orders orders = new Orders();
}
