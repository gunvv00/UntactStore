package com.untacstore.modules.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.untactstore.modules.account.Account;
import com.untactstore.modules.account.repository.AccountRepository;
import com.untactstore.modules.account.service.AccountService;
import com.untactstore.modules.keyword.Keyword;
import com.untactstore.modules.keyword.KeywordRepository;
import com.untactstore.modules.location.Location;
import com.untactstore.modules.location.LocationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
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
class AccountSettingsControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired AccountService accountService;
    @Autowired ObjectMapper objectMapper;
    @Autowired KeywordRepository keywordRepository;
    @Autowired LocationRepository locationRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @WithAccount("byungryang")
    @DisplayName("셋팅 - 프로필 - 폼")
    @Test
    void updateProfileForm() throws Exception {
        mockMvc.perform(get("/settings/profile"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithAccount("byungryang")
    @DisplayName("셋팅 - 프로필 - 수정 - 정상")
    @Test
    void updateProfile_currect() throws Exception {
        mockMvc.perform(post("/settings/profile")
                        .param("name", "송건우2")
                        .param("email", "gunvv00@naver.com")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile"))
                .andExpect(flash().attributeExists("message"))
        ;
        Account byungryang = accountRepository.findByUsername("byungryang");
        assertEquals("송건우2", byungryang.getName());
        assertEquals("gunvv00@naver.com", byungryang.getEmail());
    }

    @WithAccount("byungryang")
    @DisplayName("셋팅 - 프로필 - 수정 - 오류")
    @Test
    void updateProfile_wrong() throws Exception {
        mockMvc.perform(post("/settings/profile")
                    .param("name", "송건우2")
                    .param("email", "gunvv00...")
                    .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/profile"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors())
        ;
        Account byungryang = accountRepository.findByUsername("byungryang");
        assertNotEquals("송건우2", byungryang.getName());
        assertNotEquals("gunvv00@naver.com", byungryang.getEmail());
    }

    @WithAccount("byungryang")
    @DisplayName("셋팅 - 키워드 / 지역 - 폼")
    @Test
    void keyword_location_Form() throws Exception {
        mockMvc.perform(get("/settings/keyword-location"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("keywords"))
                .andExpect(model().attributeExists("whitelistOfKeyword"))
                .andExpect(model().attributeExists("locations"))
                .andExpect(model().attributeExists("whitelistOfLocation"))
        ;
    }

    @WithAccount("byungryang")
    @DisplayName("셋팅 - 키워드 - 계정에 추가")
    @Test
    void addKeyword() throws Exception {
        Keyword keyword = new Keyword();
        keyword.setName("newKeyword");

        mockMvc.perform(post("/settings/keywords/add")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(keyword))
                    .with(csrf()))
                .andExpect(status().isOk())
        ;
        Keyword newKeyword = keywordRepository.findByName("newKeyword");
        assertNotNull(newKeyword);
        Account byungryang = accountRepository.findByUsername("byungryang");
        assertTrue(byungryang.getKeywords().contains(newKeyword));
    }

    @WithAccount("byungryang")
    @DisplayName("셋팅 - 키워드 - 계정에서 제거")
    @Test
    void removeKeyword() throws Exception {
        Account byungryang = accountRepository.findByUsername("byungryang");
        Keyword newKeyword = keywordRepository.save(Keyword.builder().name("newKeyword").build());
        accountService.addKeyword(byungryang, newKeyword);

        assertTrue(byungryang.getKeywords().contains(newKeyword));

        Keyword keyword = new Keyword();
        keyword.setName("newKeyword");

        mockMvc.perform(post("/settings/keywords/remove")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(keyword))
                    .with(csrf()))
                .andExpect(status().isOk())
        ;
        assertFalse(byungryang.getKeywords().contains(newKeyword));
    }

    @WithAccount("byungryang")
    @DisplayName("셋팅 - 지역 - 계정에 추가")
    @Test
    void addLocation() throws Exception {
        Location location = new Location();
        location.setName("newLocation");

        mockMvc.perform(post("/settings/locations/add")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(location))
                    .with(csrf()))
                .andExpect(status().isOk())
        ;
        Location newLocation = locationRepository.findByName("newLocation");
        assertNotNull(newLocation);
        Account byungryang = accountRepository.findByUsername("byungryang");
        assertTrue(byungryang.getLocations().contains(newLocation));
    }

    @WithAccount("byungryang")
    @DisplayName("셋팅 - 지역 - 계정에서 제거")
    @Test
    void removeLocation() throws Exception {
        Account byungryang = accountRepository.findByUsername("byungryang");
        Location newLocation = locationRepository.save(Location.builder().name("newLocation").build());
        accountService.addLocation(byungryang, newLocation);

        assertTrue(byungryang.getLocations().contains(newLocation));

        Location location = new Location();
        location.setName("newLocation");

        mockMvc.perform(post("/settings/locations/remove")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(location))
                    .with(csrf()))
                .andExpect(status().isOk())
        ;
        assertFalse(byungryang.getKeywords().contains(newLocation));
    }

    @WithAccount("byungryang")
    @DisplayName("셋팅 - 알림 - 폼")
    @Test
    public void notificationsForm() throws Exception {
        mockMvc.perform(get("/settings/notifications"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("notifications"))
        ;
    }

    @WithAccount("byungryang")
    @DisplayName("셋팅 - 알림 - 수정")
    @Test
    public void updateNotifications() throws Exception {
        mockMvc.perform(post("/settings/notifications")
                        .param("ticketByWeb", "false")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/notifications"))
                .andExpect(flash().attributeExists("message"))
        ;

        Account byungryang = accountRepository.findByUsername("byungryang");
        assertEquals(false, byungryang.isTicketByWeb());
    }

    @WithAccount("byungryang")
    @DisplayName("셋팅 - 계정 - 폼")
    @Test
    public void accountForm() throws Exception {
        mockMvc.perform(get("/settings/account"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"))
        ;
    }

    @WithAccount("byungryang")
    @DisplayName("셋팅 - 계정 - 패스워드 수정")
    @Test
    public void updatePassword() throws Exception {
        mockMvc.perform(post("/settings/password")
                    .param("newPassword", "qwerasdf")
                    .param("newPasswordConfirm", "qwerasdf")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/account"))
                .andExpect(flash().attributeExists("message"))
        ;

        Account byungryang = accountRepository.findByUsername("byungryang");
        assertTrue(passwordEncoder.matches("qwerasdf", byungryang.getPassword()));
    }
}