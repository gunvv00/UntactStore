package com.untactstore.modules.table;

import com.untactstore.modules.account.Account;
import com.untactstore.modules.store.Store;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly=true)
public interface TablesRepository extends JpaRepository<Tables, Long> {


    Tables findFirstByStoreOrderByTableNumDesc(Store store);

    List<Tables> findByAccount(Account accountLoaded);

    //table view
    @EntityGraph(attributePaths = {"account", "eventList"})
    Tables findTablesWithAccountAndEventByStoreAndTablesPath(Store store, String tablesPath);


    Tables findByStoreAndTablesPath(Store store, String tablesPath);
}
