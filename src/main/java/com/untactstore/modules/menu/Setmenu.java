package com.untactstore.modules.menu;

import com.untactstore.modules.store.Store;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor
public class Setmenu {
    @Id @GeneratedValue
    private Long id;

    private String title;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String image;

    @ManyToOne
    private Store store;

    @OneToMany
    private Set<Menu> menuList = new HashSet<>();

    private Integer totalPrice;

    private String explanation;
}
