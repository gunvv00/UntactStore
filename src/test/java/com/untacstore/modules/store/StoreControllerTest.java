package com.untacstore.modules.store;

import com.untactstore.modules.account.Account;
import com.untacstore.modules.account.WithAccount;
import com.untactstore.modules.account.repository.AccountRepository;
import com.untactstore.modules.keyword.KeywordRepository;
import com.untactstore.modules.menu.repository.MenuRepository;
import com.untactstore.modules.menu.repository.SetmenuRepository;
import com.untactstore.modules.store.Store;
import com.untactstore.modules.store.form.StoreForm;
import com.untactstore.modules.store.repository.StoreRepository;
import com.untactstore.modules.store.service.StoreService;
import com.untactstore.modules.table.TablesRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class StoreControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    StoreService storeService;
    @Autowired
    KeywordRepository keywordRepository;
    @Autowired
    TablesRepository tablesRepository;
    @Autowired
    MenuRepository menuRepository;
    @Autowired
    SetmenuRepository setmenuRepository;
    @Autowired
    AccountRepository accountRepository;

    @WithAccount("byungryang")
    @DisplayName("상점 생성 - 폼")
    @Test
    void createStoreForm() throws Exception {
        mockMvc.perform(get("/store/new-store"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("storeForm"))
        ;
    }

    @WithAccount("byungryang")
    @DisplayName("상점 생성 - 생성")
    @Test
    void createStore() throws Exception {
        String fullDescription = "asdfffffffffffffffffffffffffffff";
        mockMvc.perform(post("/store/new-store")
                    .param("licensee", "licensee")
                    .param("path", "store-path")
                    .param("name", "송건우")
                    .param("address", "address")
                    .param("shortDescription", "shortDescription")
                    .param("fullDescription", fullDescription)
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/store/store-path"))
        ;
        Store store = storeRepository.findStoreByPath("store-path");
        assertEquals("licensee", store.getLicensee());
        assertEquals("store-path", store.getPath());
        assertEquals("송건우", store.getName());
        assertEquals("shortDescription", store.getShortDescription());
        assertEquals(fullDescription, store.getFullDescription());
    }

    @WithAccount("byungryang")
    @DisplayName("상점 - 정보")
    @Test
    void StoreMypage() throws Exception {
        StoreForm storeForm = new StoreForm();
        storeForm.setLicensee("licensee");
        storeForm.setPhone("010-2228-5677");
        storeForm.setName("name");
        storeForm.setPath("store-path");
        storeForm.setShortDescription("shortDescription");
        storeForm.setFullDescription("fullDescription");

        Account account = accountRepository.findByUsername("byungryang");
        storeService.newStore(account, storeForm);

        mockMvc.perform(get("/store/store-path"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("store"))
                .andExpect(model().attributeExists("review"))
                .andExpect(model().attributeExists("isOwner"))
        ;
    }
}