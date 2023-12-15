package com.untactstore.modules.menu.service;

import com.untactstore.modules.menu.Menu;
import com.untactstore.modules.menu.repository.MenuRepository;
import com.untactstore.modules.store.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;

    public void addMenu(Store store, Menu newMenu) {
        newMenu.setStore(store);
        menuRepository.save(newMenu);
    }

    public void removeMenu(Long id) {
        menuRepository.deleteById(id);
    }
}
