package com.untactstore.modules.account;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder
public class AccountReport {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Account to;

    @ManyToOne
    private Account from;
}
