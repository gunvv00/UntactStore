package com.untactstore.modules.store.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.untactstore.modules.account.Account;
import com.untactstore.modules.account.authentication.CurrentAccount;
import com.untactstore.modules.grade.Grade;
import com.untactstore.modules.grade.GradeRepository;
import com.untactstore.modules.menu.Menu;
import com.untactstore.modules.menu.Setmenu;
import com.untactstore.modules.menu.repository.MenuRepository;
import com.untactstore.modules.menu.repository.SetmenuRepository;
import com.untactstore.modules.order.Payment;
import com.untactstore.modules.order.PaymentRepository;
import com.untactstore.modules.order.form.Sales;
import com.untactstore.modules.order.form.Statistics;
import com.untactstore.modules.review.ReplyForm;
import com.untactstore.modules.review.Review;
import com.untactstore.modules.review.ReviewForm;
import com.untactstore.modules.review.ReviewRepository;
import com.untactstore.modules.store.Store;
import com.untactstore.modules.store.form.RemoteControl;
import com.untactstore.modules.store.form.StoreForm;
import com.untactstore.modules.store.repository.StoreRepository;
import com.untactstore.modules.store.service.StoreService;
import com.untactstore.modules.store.validator.StoreFormValidator;
import com.untactstore.modules.table.Tables;
import com.untactstore.modules.table.TablesRepository;
import com.untactstore.modules.waiting.WaitingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/store")
@SessionAttributes("remoteControl")
@Transactional
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;
    private final StoreRepository storeRepository;
    private final StoreFormValidator storeFormValidator;
    private final MenuRepository menuRepository;
    private final SetmenuRepository setmenuRepository;
    private final ReviewRepository reviewRepository;
    private final PaymentRepository paymentRepository;
    private final TablesRepository tablesRepository;
    private final GradeRepository gradeRepository;
    private final WaitingRepository waitingRepository;
    private final ObjectMapper objectMapper;

    @InitBinder("storeForm")
    public void initBinder_storeForm(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(storeFormValidator);
    }

    /*Store - 폼*/
    @GetMapping("/new-store")
    public String createStoreForm(@CurrentAccount Account account, Model model) {
        if (account == null) {
            return "redirect:/";
        }
        model.addAttribute(account);
        model.addAttribute(new StoreForm());
        return "store/form";
    }

    /*Store - 생성*/
    @PostMapping("/new-store")
    public String createStore(@CurrentAccount Account account, @Valid StoreForm storeForm, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "store/form";
        }
        storeService.newStore(account, storeForm);

        return "redirect:/store/"+ URLEncoder.encode(storeForm.getPath(), StandardCharsets.UTF_8);
    }

    /*내 상점*/
    @GetMapping("mystore")
    public String myStorePage(@CurrentAccount Account account) {
        Store store = storeRepository.findByOwner(account);

        return "redirect:/store/"+ URLEncoder.encode(store.getPath(), StandardCharsets.UTF_8);
    }

    /*Store view*/
    @GetMapping("/{path}")
    public String storePageView(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Store store = storeRepository.findStoreWithKeywordsAndWaitingListAndFavoritesListByPath(path);
        model.addAttribute(store);
        if (account != null) {
            model.addAttribute(account);
            model.addAttribute("isOwner", account.getUsername().equals(store.getOwner().getUsername()));
        } else {
            model.addAttribute("isOwner", false);
        }
        //리뷰
        Set<Review> reviews = store.getReviews();
        model.addAttribute("review", reviews);

        Grade grade = gradeRepository.findByAccountAndStore(account, store);
        if (grade != null) {
            model.addAttribute("grade", grade.getGrade());
        }

        return "store/view";
    }

    /*menu 소개*/
    @GetMapping("/{path}/menu")
    public String menuView(@CurrentAccount Account account, @PathVariable String path, Model model) {
        if (account != null) {
            model.addAttribute(account);
        }

        Store store = storeRepository.findStoreWithKeywordsAndWaitingListAndFavoritesListByPath(path);
        model.addAttribute(store);

        List<Setmenu> setmenuList = setmenuRepository.findSetmenuWithMenuListByStore(store);
        model.addAttribute("setmenuList", setmenuList);

        List<Menu> menuList = menuRepository.findByStore(store);
        model.addAttribute("menuList", menuList);
        return "store/menu";
    }

    /*리뷰게시판 - 폼*/
    @GetMapping("/{path}/review")
    public String reviewView(@CurrentAccount Account account, @PathVariable String path, Model model) {
        if (account != null) {
            model.addAttribute(account);
        }
        Store store = storeRepository.findStoreWithKeywordsAndWaitingListAndFavoritesListByPath(path);
        model.addAttribute(store);

        List<Review> review = reviewRepository.findReviewWithReplyByStoreOrderByReviewAt(store);
        model.addAttribute("reviews", review);

        model.addAttribute("reviewForm", new ReviewForm());

        model.addAttribute("replyForm", new ReplyForm());

        return "store/review";
    }

    /*리뷰게시판 - 리뷰 등록*/
    @PostMapping("/{path}/review/add")
    public String addReview(@CurrentAccount Account account, ReviewForm reviewForm, @PathVariable String path, Model model) {
        Store store = storeRepository.findStoreByPath(path);

        storeService.addReview(account, store, reviewForm);

        return "redirect:/store/"+ URLEncoder.encode(path, StandardCharsets.UTF_8)+"/review";
    }
    //TODO 댓글 삭제

    /*리뷰게시판 - 답글 등록*/
    @PostMapping("/{path}/reply/add")
    public String addReply(@CurrentAccount Account account, ReplyForm replyForm, @PathVariable String path, Model model) {
        Store store = storeRepository.findStoreByPath(path);

        storeService.addReply(account, store, replyForm);

        return "redirect:/store/"+ URLEncoder.encode(path, StandardCharsets.UTF_8)+"/review";
    }
    //TODO 답글 삭제

    /*통계 - 뷰*/
    @GetMapping("/{path}/management")
    public String managementView(@CurrentAccount Account account, RemoteControl remoteControl, @PathVariable String path, Model model) throws JsonProcessingException {
        model.addAttribute(account);
        Store store = storeRepository.findStoreByPath(path);
        model.addAttribute(store);
        List<Payment> paymentList;
        if (remoteControl.getTableNo().equals("all")) {
            paymentList = paymentRepository.findPaymentWithOrderListByStoreOrderByPaymentAtDesc(store);

        } else {
            Tables tables = tablesRepository.findByStoreAndTablesPath(store, remoteControl.getTableNo());
            paymentList = paymentRepository.findPaymentWithOrderListByStoreAndTablesOrderByPaymentAtDesc(store, tables);
        }
        model.addAttribute("paymentList", paymentList);

        putStatistics(paymentList, model);

        List<Sales> salesList = new ArrayList<>();
        //년-월-일 list
        paymentList.stream().forEach(p-> {
            boolean flag = false;
            int index = 0;
            for (Sales s: salesList) {
                if (s.getDate().equals(LocalDate.from(p.getPaymentAt()))) {
                    s.setSales(s.getSales()+p.getPay());
                    flag = true;
                    break;
                } else {
                    flag = false;
                }
            }
            if (!flag) {
                salesList.add(Sales.builder().date(LocalDate.from(p.getPaymentAt())).sales(p.getPay()).build());
            }
        });
        String chartData = objectMapper.writeValueAsString(salesList);
        System.out.println(chartData);
        model.addAttribute("chartData", chartData);

        model.addAttribute(remoteControl);
        return "store/management";
    }

    private void putStatistics(List<Payment> paymentList, Model model) {
        List<Setmenu> setmenuList = new ArrayList<>();
        List<Menu> menuList = new ArrayList<>();
        paymentList.stream().forEach(p->{
            p.getOrderList().stream().forEach(o->{
                setmenuList.addAll(o.getSetMenuList());
                menuList.addAll(o.getMenuList());
            });
        });
        Map<Long, Statistics> statistics = new HashMap<>();
        setmenuList.stream().forEach(sm->{
            if (statistics.containsKey(sm.getId())) {
                statistics.get(sm.getId()).addCount();
            } else {
                statistics.put(sm.getId(), Statistics.builder().name(sm.getTitle()).price(sm.getTotalPrice()).count(1).build());
            }
        });
        menuList.stream().forEach(m->{
            if (statistics.containsKey(m.getId())) {
                statistics.get(m.getId()).addCount();
            } else {
                statistics.put(m.getId(), Statistics.builder().name(m.getName()).price(m.getPrice()).count(1).build());
            }
        });
        model.addAttribute("statistics", statistics.values().stream().collect(Collectors.toList()));
    }

    /*별점 설정*/
    @GetMapping("/{path}/grade")
    public String setGrade(@CurrentAccount Account account, @PathVariable String path, String grade, Model model) {
        Store store = storeRepository.findStoreByPath(path);
        storeService.setGrade(account, store, grade);
        return "redirect:/store/"+ URLEncoder.encode(path, StandardCharsets.UTF_8);
    }
}
