package com.untactstore.modules.account.repository;

import com.untactstore.modules.account.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long>, QuerydslPredicateExecutor<Account> {
    @EntityGraph(attributePaths = {"locations"})
    Account findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String username);

    @EntityGraph(attributePaths = {"keywords"})
    Account findAccountWithKeywordsById(Long id);

    @EntityGraph(attributePaths = {"keywords", "locations"})
    Account findAccountWithKeywordsAndLocationsById(Long id);

    Account findByEmail(String email);

    @EntityGraph(attributePaths = {"favoritesList", "favoritesList.store"})
    Account findAccountWithFavoritesListByUsername(String username);
}
