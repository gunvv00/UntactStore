package com.untactstore.modules.order.form;

import lombok.*;

@Data
@Builder
public class Statistics {
    private String name;
    private Integer price;
    private Integer count;
    public void addCount() {
        count++;
    }
}
