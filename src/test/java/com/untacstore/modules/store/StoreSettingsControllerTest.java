package com.untacstore.modules.store;

import com.untactstore.modules.keyword.KeywordRepository;
import com.untactstore.modules.menu.repository.MenuRepository;
import com.untactstore.modules.menu.repository.SetmenuRepository;
import com.untactstore.modules.store.repository.StoreRepository;
import com.untactstore.modules.table.TablesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class StoreSettingsControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    KeywordRepository keywordRepository;
    @Autowired
    TablesRepository tablesRepository;
    @Autowired
    MenuRepository menuRepository;
    @Autowired
    SetmenuRepository setmenuRepository;

    final String path = "/store/{path}/settings";


}