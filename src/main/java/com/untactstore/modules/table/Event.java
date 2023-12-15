package com.untactstore.modules.table;

import com.untactstore.modules.account.Account;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor
public class Event {
    @Id @GeneratedValue
    private Long id;

    private Integer turn;

    private Integer personnel;

    @ManyToOne
    private Tables tables;

    @ManyToOne
    private Account account;

    private LocalDateTime eventAt;

    private boolean accept = false;
}
