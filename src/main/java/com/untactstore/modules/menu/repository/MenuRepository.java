package com.untactstore.modules.menu.repository;

import com.untactstore.modules.menu.Menu;
import com.untactstore.modules.store.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByStore(Store store);
}
