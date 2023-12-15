package com.untactstore.modules.table;

import com.untactstore.modules.account.Account;
import com.untactstore.modules.account.authentication.PrincipalAccount;
import com.untactstore.modules.order.Orders;
import com.untactstore.modules.store.Store;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Tables {
    @Id @GeneratedValue
    private Long id;

    private Integer tableNum;

    private String tablesPath;

    @ManyToOne
    private Store store;

    @ManyToOne
    private Account account; //현재 손님

    private Integer personnel; //정원

    private LocalDateTime startedAt = null;

    private Integer pay = null; //가격

    @OneToMany(mappedBy = "tables")
    @OrderBy("orderAt")
    private List<Orders> orderList = new ArrayList<>(); //주문목록

    @OneToMany(mappedBy = "tables")
    @OrderBy("eventAt")
    private List<Event> eventList = new ArrayList<>();

    public boolean requestPayment = false;

    public boolean isSitable() {
        return this.account == null;
    }

    public void addEvent(Event event) {
        eventList.add(event);
        event.setTables(this);
    }

    public void removeEvent(Event event) {
        eventList.remove(event);
    }

    public boolean checkSit(PrincipalAccount principalAccount) {
        Account account = principalAccount.getAccount();
        return eventList.stream().filter(e->e.getAccount().equals(account)).collect(Collectors.toList()).isEmpty();
    }

    public boolean checkAccount(PrincipalAccount principalAccount) {
        if (account == null) {
            return false;
        }
        return this.account.equals(principalAccount.getAccount());
    }

    public boolean isfull() {
        return this.account!=null;
    }
}
