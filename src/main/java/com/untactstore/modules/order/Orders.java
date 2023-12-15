package com.untactstore.modules.order;

import com.untactstore.modules.menu.Menu;
import com.untactstore.modules.menu.Setmenu;
import com.untactstore.modules.table.Tables;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor
public class Orders {
    @Id @GeneratedValue
    private Long id;

    private String title;

    @ManyToOne
    private Tables tables;

    private Integer ordersAmount = 0;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Menu> menuList = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Setmenu> setMenuList = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<RequestOrder> requestOrderList = new HashSet<>();

    private LocalDateTime orderAt;

    @Enumerated(EnumType.STRING)
    private OrderStatusType orderStatusType = null;

    public String getStatus() {
        if (orderStatusType.equals(OrderStatusType.NEW_ORDER)) {
            return "확인전";
        } else if (orderStatusType.equals(OrderStatusType.BEFORE_COMPLETE)) {
            return "진행중";
        } else if (orderStatusType.equals(OrderStatusType.AFTER_COMPLETE)) {
            return "완료";
        } else if (orderStatusType.equals(OrderStatusType.COMPLETE_PAYMENT)) {
            return "결제완료";
        }
        return "";
    }
}