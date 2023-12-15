package com.untactstore.modules.order;

import com.untactstore.modules.table.Tables;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly=true)
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    void deleteAllByTables(Tables tables);


    List<Orders> findAllByTablesOrderByOrderAtDesc(Tables tables);

    //table view
    @EntityGraph(attributePaths = {"setMenuList", "menuList", "requestOrderList"})
    List<Orders> findOrdersWithSetmenuListAndMenuListAndRequestOrderListByTablesAndOrderStatusTypeIsNot(Tables tables, OrderStatusType type);

    //table management
    @EntityGraph(attributePaths = {"setMenuList", "setMenuList.menuList", "menuList", "requestOrderList"})
    List<Orders> findOrdersWithSetmenuListAndMenuListAndRequestOrderListByTables(Tables tables);
}
