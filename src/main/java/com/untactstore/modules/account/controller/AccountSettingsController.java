package com.untactstore.modules.account.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.untactstore.modules.account.Account;
import com.untactstore.modules.account.authentication.CurrentAccount;
import com.untactstore.modules.account.form.Notifications;
import com.untactstore.modules.account.form.PasswordForm;
import com.untactstore.modules.account.form.Profile;
import com.untactstore.modules.account.repository.AccountRepository;
import com.untactstore.modules.account.service.AccountService;
import com.untactstore.modules.account.validator.PasswordFormValidator;
import com.untactstore.modules.keyword.Keyword;
import com.untactstore.modules.keyword.KeywordForm;
import com.untactstore.modules.keyword.KeywordRepository;
import com.untactstore.modules.keyword.KeywordService;
import com.untactstore.modules.location.Location;
import com.untactstore.modules.location.LocationForm;
import com.untactstore.modules.location.LocationRepository;
import com.untactstore.modules.location.LocationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
public class AccountSettingsController {
    static final String ROOT = "/";
    static final String SETTINGS = "settings";
    static final String PROFILE = "/profile";
    static final String KEYWORD_LOCATION = "/keyword-location";
    static final String NOTIFICATIONS = "/notifications";
    static final String PASSWORD = "/password";
    static final String ACCOUNT = "/account";

    private final ModelMapper modelMapper;
    private final AccountService accountService;
    private final KeywordRepository keywordRepository;
    private final KeywordService keywordService;
    private final LocationRepository locationRepository;
    private final AccountRepository accountRepository;
    private final LocationService locationService;
    private final ObjectMapper objectMapper;

    @InitBinder("passwordForm")
    public void passwordFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new PasswordFormValidator());
    }

    /*프로필 - 폼*/
    @GetMapping(PROFILE)
    public String profileForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, Profile.class));
        return SETTINGS + PROFILE;
    }
    /*프로필 - 수정*/
    @PostMapping(PROFILE)
    public String updateProfile(@CurrentAccount Account account, @Valid Profile profile, Errors errors, Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS + PROFILE;
        }
        accountService.updateProfile(account, profile);
        attributes.addFlashAttribute("message", "프로필 정보를 수정했습니다.");
        return "redirect:/" + SETTINGS + PROFILE;
    }

    /*키워드 / 지역 - 폼*/
    @GetMapping(KEYWORD_LOCATION)
    public String keyword_locationForm(@CurrentAccount Account account, Model model) throws JsonProcessingException {
        Account accountLoad = accountRepository.findAccountWithKeywordsAndLocationsById(account.getId());
        model.addAttribute(accountLoad);

        //로그인된 계정의 keyword
        Set<Keyword> keywords = accountService.getKeywords(accountLoad);
        model.addAttribute("keywords", keywords.stream().map(Keyword::getName).collect(Collectors.toList()));
        //전체 keyword
        List<String> allKeywords = keywordRepository.findAll().stream().map(Keyword::getName).collect(Collectors.toList());
        model.addAttribute("whitelistOfKeyword", objectMapper.writeValueAsString(allKeywords));

        //로그인된 계정의 location
        Set<Location> locations = accountService.getLocations(accountLoad);
        model.addAttribute("locations", locations.stream().map(Location::getName).collect(Collectors.toList()));
        //전체 location
        List<String> allLocation = locationRepository.findAll().stream().map(Location::getName).collect(Collectors.toList());
        model.addAttribute("whitelistOfLocation", objectMapper.writeValueAsString(allLocation));

        System.out.println("log");
        return SETTINGS + KEYWORD_LOCATION;
    }
    /*키워드 추가*/
    @PostMapping("/keywords/add")
    @ResponseBody
    public ResponseEntity addKeyword(@CurrentAccount Account account, @RequestBody KeywordForm keywordForm) {
        System.out.println("ajax keyword");
        System.out.println(keywordForm.getName());
        Keyword keyword = keywordService.findOrCreateNew(keywordForm.getName());
        if (keyword == null) {
            return ResponseEntity.badRequest().build();
        }

        accountService.addKeyword(account, keyword);
        return ResponseEntity.ok().build();
    }
    /*키워드 삭제*/
    @PostMapping("/keywords/remove")
    @ResponseBody
    public ResponseEntity removeKeyword(@CurrentAccount Account account, @RequestBody KeywordForm keywordForm) {
        String name = keywordForm.getName();
        Keyword keyword = keywordRepository.findByName(name);
        if (keyword == null) {
            return ResponseEntity.badRequest().build();
        }

        accountService.removeKeyword(account, keyword);
        return ResponseEntity.ok().build();
    }
    /*지역 추가*/
    @PostMapping("/locations/add")
    @ResponseBody
    public ResponseEntity addLocation(@CurrentAccount Account account, @RequestBody LocationForm locationForm) {
        System.out.println("ajax location");
        Location location = locationService.findOrCreateNew(locationForm.getName());
        if (location == null) {
            return ResponseEntity.badRequest().build();
        }

        accountService.addLocation(account, location);
        return ResponseEntity.ok().build();
    }
    /*지역 삭제*/
    @PostMapping("/locations/remove")
    @ResponseBody
    public ResponseEntity removeLocation(@CurrentAccount Account account, @RequestBody LocationForm locationForm) {
        String name = locationForm.getName();
        Location location = locationRepository.findByName(name);
        if (location == null) {
            return ResponseEntity.badRequest().build();
        }
        accountService.removeLocation(account, location);
        return ResponseEntity.ok().build();
    }

    /*알림설정 - 폼*/
    @GetMapping(NOTIFICATIONS)
    public String updateNotificationsForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, Notifications.class));
        return SETTINGS + NOTIFICATIONS;
    }

    /*알림설정 - 수정*/
    @PostMapping(NOTIFICATIONS)
    public String updateNotifications(@CurrentAccount Account account, @Valid Notifications notifications, Errors errors,
                                      Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS + NOTIFICATIONS;
        }

        accountService.updateNotifications(account, notifications);
        attributes.addFlashAttribute("message", "알림 설정을 변경했습니다.");
        return "redirect:/" + SETTINGS + NOTIFICATIONS;
    }

    /*계정 - 폼*/
    @GetMapping(ACCOUNT)
    public String passwordForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());

        return SETTINGS + ACCOUNT;
    }

    /*계정 - 패스워드 수정*/
    @PostMapping(PASSWORD)
    public String updatePassword(@CurrentAccount Account account, @Valid PasswordForm passwordForm, Errors errors, Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS + ACCOUNT;
        }
        accountService.updatePassword(account, passwordForm.getNewPassword());
        attributes.addFlashAttribute("message", "패스워드 정보를 수정했습니다.");
        return "redirect:/" + SETTINGS + ACCOUNT;
    }
    /*계정 - 탈퇴*/
    @PostMapping(ACCOUNT)
    public String updatePassword(@CurrentAccount Account account) {
//        accountService.deleteAccount(account);
        return "redirect:/" + SETTINGS + ACCOUNT;
    }
}
