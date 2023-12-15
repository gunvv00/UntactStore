package com.untactstore.modules.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public interface AccountReportRepository extends JpaRepository<AccountReport, Long> {
    void deleteByToAndFrom(Account to, Account from);

    AccountReport findByToAndFrom(Account to, Account from);
}
