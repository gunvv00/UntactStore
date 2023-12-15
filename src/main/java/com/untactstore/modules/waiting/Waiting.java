package com.untactstore.modules.waiting;

import com.untactstore.modules.account.Account;
import com.untactstore.modules.account.authentication.PrincipalAccount;
import com.untactstore.modules.store.Store;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor
public class Waiting {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Store store;

    @ManyToOne
    private Account account;

    private Integer personnel;

    private Integer turn; //순번

    private boolean available = false; //유효한 티켓인지

    private boolean attended = false;

    private LocalDateTime waitingAt;

    public boolean isAccount(PrincipalAccount principalAccount) {
        Account account = principalAccount.getAccount();
        return account.equals(this.account);
    }

}
