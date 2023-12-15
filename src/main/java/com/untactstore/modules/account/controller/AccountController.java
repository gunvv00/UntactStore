package com.untactstore.modules.account.controller;

import com.untactstore.modules.account.Account;
import com.untactstore.modules.account.authentication.CurrentAccount;
import com.untactstore.modules.account.form.FindPasswordForm;
import com.untactstore.modules.account.form.SignUpForm;
import com.untactstore.modules.account.repository.AccountRepository;
import com.untactstore.modules.account.service.AccountService;
import com.untactstore.modules.account.validator.SignUpFormValidator;
import com.untactstore.modules.favorites.Favorites;
import com.untactstore.modules.keyword.Keyword;
import com.untactstore.modules.location.Location;
import com.untactstore.modules.store.Store;
import com.untactstore.modules.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final StoreRepository storeRepository;
    private final SignUpFormValidator signUpFormValidator;

    @InitBinder("signUpForm")
    public void initBinder_signUpForm(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    /*회원가입 - 폼*/
    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute("signUpForm", new SignUpForm());
        return "account/sign-up";
    }
    /*회원가입*/
    @PostMapping("/sign-up")
    public String signUp(@Valid SignUpForm signUpForm, Errors errors, Model model, HttpServletRequest request) {
        if (errors.hasErrors()) {
            //SignUpFormValidator 에서 검사
            return "account/sign-up";
        }

        Account account = accountService.processNewAccount(signUpForm);
        accountService.login(account);
        return "redirect:/";
    }

    /*프로필 보기*/
    @GetMapping("/profile/{username}")
    public String viewProfile(@CurrentAccount Account account, @PathVariable String username, String view, Model model) {
        model.addAttribute(account);
        Account accountToView = accountService.getAccount(username);
        model.addAttribute("profileAccount", accountToView);
        model.addAttribute("isOwner", accountToView.equals(account));

//        Set<Keyword> keywords = accountService.getKeywords(account);
//        model.addAttribute("keywords", keywords.stream().map(Keyword::getName).collect(Collectors.toList()));
//        //로그인된 계정의 location
//        Set<Location> locations = accountService.getLocations(account);
//        model.addAttribute("locations", locations.stream().map(Location::getName).collect(Collectors.toList()));

        List<Store> favoritesStoreList = accountToView.getFavoritesList().stream().map(Favorites::getStore).collect(Collectors.toList());
        model.addAttribute("favoritesStoreList", favoritesStoreList);

        model.addAttribute("profileUsername", username);

        if (view == null) {
            model.addAttribute("view", "profile");
        } else {
            model.addAttribute("view", view);
        }
        return "account/profile";
    }

    /*계정 - 신고*/
    @GetMapping("/profile/{username}/report")
    public String addReport(@CurrentAccount Account account, @PathVariable String username, Model model) {
        Account to = accountRepository.findByUsername(username);
        accountService.addReport(to, account);

        return "redirect:/profile/"+username;
    }
    /*계정 - 신고취소*/
    @GetMapping("/profile/{username}/report-cancel")
    public String cancelReport(@CurrentAccount Account account, @PathVariable String username, Model model) {
        Account to = accountRepository.findByUsername(username);
        accountService.cancelReport(to, account);

        return "redirect:/profile/"+username;
    }

    /*계정 - 비밀번호 찾기*/
    @GetMapping("/find-password")
    public String findPasswordForm(Model model) {
        model.addAttribute(new FindPasswordForm());
        return "account/find-password";
    }

    @PostMapping("/find-password")
    public String findPassword(@Valid FindPasswordForm findPasswordForm, Errors errors, Model model, RedirectAttributes attributes) {
        Account account = accountRepository.findByUsername(findPasswordForm.getUsername());
        if (account == null) {
            model.addAttribute("error", "유효한 아이디가 아닙니다.");
            return "account/find-password";
        }

        if (!account.getEmail().equals(findPasswordForm.getEmail())) {
            model.addAttribute("error", "등록하신 이메일이 아닙니다.");
            return "account/find-password";
        }


        accountService.sendFindPasswordEmail(account);
        attributes.addFlashAttribute("message", "입력하신 이메일로 인증 메일을 발송했습니다.");
        return "redirect:/";
    }

    @GetMapping("/find-password-by-email")
    public String findPasswordByEmail(String token, String email, Model model) {
        Account account = accountRepository.findByEmail(email);
        if (account == null || !account.isValidToken(token)) {
            model.addAttribute("error", "로그인 할 수 없습니다.");
            return "account/logged-in-by-email";
        }
        accountService.login(account);
        return "redirect:/settings/account";
    }

    @GetMapping("/addToken")
    public String addToken() {
        List<Account> accountList = accountRepository.findAll();
        accountService.addToken(accountList);

        return "redirect:/";
    }
}
