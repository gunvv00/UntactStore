package com.untactstore.modules.favorites;

import com.untactstore.modules.account.Account;
import com.untactstore.modules.store.Store;
import lombok.*;

import javax.persistence.*;

@Entity @EqualsAndHashCode(of = "id")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Favorites {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Account account; //사용자

    @ManyToOne
    private Store store; //가게
}
