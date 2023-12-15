package com.untactstore.modules.account;

import com.untactstore.modules.account.authentication.PrincipalAccount;
import com.untactstore.modules.favorites.Favorites;
import com.untactstore.modules.keyword.Keyword;
import com.untactstore.modules.location.Location;
import com.untactstore.modules.waiting.Waiting;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Account {
    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String username;
    private String password;
    private String name;

    @Column(unique = true)
    private String email;
    private boolean emailVerified; //이메일 검증 확인
    private String emailCheckToken; //이메일 검증 토큰
    private LocalDateTime emailVerifiedDateTime; //회원가입 시간

    private boolean hasStore = false;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    //keyword
    @ManyToMany
    private Set<Keyword> keywords = new HashSet<>();

    //location 관심지역
    @ManyToMany
    private Set<Location> locations = new HashSet<>();

    private boolean storeCreatedByWeb = true;
    private boolean ticketByWeb = true;

    @OneToMany(mappedBy = "account")
    private List<Waiting> waitingList = new ArrayList<>();

    @OneToMany(mappedBy = "to")
    private List<AccountReport> report = new ArrayList<>();

    @OneToMany(mappedBy = "account")
    private Set<Favorites> favoritesList = new HashSet<>();

    public int reportSize() {
        return report.size();
    }

    public boolean isReportable(PrincipalAccount principalAccount) {
        Account account = principalAccount.getAccount();
        return report.stream().filter(r->r.getFrom().equals(account)).collect(Collectors.toList()).isEmpty();
    }

    public boolean hasStore() {
        return hasStore;
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
    }

//    public boolean hasAvailableWaiting() {
//        return waitingList.stream().filter(w-> w.isAvailable()).collect(Collectors.toList()).isEmpty();
//        return waitingList.stream().filter(w-> w.isAvailable()).collect(Collectors.toList()).isEmpty();
//    }
}
