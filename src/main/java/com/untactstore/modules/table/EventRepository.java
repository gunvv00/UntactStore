package com.untactstore.modules.table;

import com.untactstore.modules.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public interface EventRepository extends JpaRepository<Event, Long> {
    Event findByTablesAndAccount(Tables tables, Account account);

    void deleteAllByTables(Tables tables);

}
