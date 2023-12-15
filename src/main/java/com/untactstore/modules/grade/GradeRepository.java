package com.untactstore.modules.grade;

import com.untactstore.modules.account.Account;
import com.untactstore.modules.store.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface GradeRepository extends JpaRepository<Grade, Long> {
    Grade findByAccountAndStore(Account account, Store store);
}
