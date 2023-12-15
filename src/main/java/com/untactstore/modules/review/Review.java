package com.untactstore.modules.review;

import com.untactstore.modules.account.Account;
import com.untactstore.modules.store.Store;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor
public class Review {
    @Id @GeneratedValue
    private Long id;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String content;

    @ManyToOne
    private Store store;

    @ManyToOne
    private Account account;

    private LocalDateTime reviewAt;

    private Integer likeReview = 0;//좋아요
//    private Integer report = 0; //신고 TODO 엔티티로 바꿔야함

    @OneToMany(mappedBy = "review")
    @OrderBy("replyAt")
    private List<Reply> replies = new ArrayList<>();
}
