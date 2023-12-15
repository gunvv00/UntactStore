package com.untactstore.modules.order;

import com.untactstore.modules.store.Store;
import com.untactstore.modules.table.Tables;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly=true)
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByTables(Tables tables);

    List<Payment> findAllByStore(Store store);

    List<Payment> findAllByStoreAndTables(Store store, Tables tables);




//    select * form payment where store_id='store' and tables_id='tables' and startedAt > '2020-05-27' and paymentAt < '2020-05-28';

    //store management
    //order, order, order
    @EntityGraph(attributePaths = {"orderList"})
    List<Payment> findPaymentWithOrderListByStoreOrderByPaymentAtDesc(Store store);

    //store management table one
    @EntityGraph(attributePaths = {"orderList"})
    List<Payment> findPaymentWithOrderListByStoreAndTablesOrderByPaymentAtDesc(Store store, Tables tables);
}
