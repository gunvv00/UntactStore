package com.untactstore.modules.account;

import com.querydsl.core.types.Predicate;
import com.untactstore.modules.account.QAccount;
import com.untactstore.modules.keyword.Keyword;

import java.util.Set;

public class AccountPredicates {
    public static Predicate findByKeywords(Set<Keyword> keywords) {
        return QAccount.account.keywords.any().in(keywords);
    }
}
