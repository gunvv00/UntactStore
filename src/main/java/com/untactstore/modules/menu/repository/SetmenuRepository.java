package com.untactstore.modules.menu.repository;

import com.untactstore.modules.menu.Setmenu;
import com.untactstore.modules.store.Store;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SetmenuRepository extends JpaRepository<Setmenu, Long> {
    @EntityGraph(attributePaths = {"menuList"})
    List<Setmenu> findSetmenuWithMenuListByStore(Store store);
}
