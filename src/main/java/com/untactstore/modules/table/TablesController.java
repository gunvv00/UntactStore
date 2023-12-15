package com.untactstore.modules.table;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.untactstore.modules.account.Account;
import com.untactstore.modules.account.authentication.CurrentAccount;
import com.untactstore.modules.account.repository.AccountRepository;
import com.untactstore.modules.menu.Menu;
import com.untactstore.modules.menu.Setmenu;
import com.untactstore.modules.menu.repository.MenuRepository;
import com.untactstore.modules.menu.repository.SetmenuRepository;
import com.untactstore.modules.store.Store;
import com.untactstore.modules.store.repository.StoreRepository;
import com.untactstore.modules.order.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/store/{path}/tables")
@RequiredArgsConstructor
@SessionAttributes("cart")
public class TablesController {
    private final TablesRepository tablesRepository;
    private final StoreRepository storeRepository;
    private final TablesService tablesService;
    private final SetmenuRepository setmenuRepository;
    private final MenuRepository menuRepository;
    private final OrdersRepository ordersRepository;
    private final EventRepository eventRepository;
    private final AccountRepository accountRepository;
    private final RequestOrderRepository requestOrderRepository;
    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper;

    /*테이블 리스트 - 뷰*/
    @GetMapping("/list")
    public String tablesListView(@CurrentAccount Account account, @PathVariable("path") String path, Model model) {
        model.addAttribute(account);
        Store store = storeRepository.findStoreByPath(path);
        model.addAttribute(store);

        model.addAttribute("tablesList", store.getTableList());
        return "tables/list";
    }

    /*테이블 - 뷰*/
    @GetMapping("/{tables-path}")
    public String tablesView(@CurrentAccount Account account, @PathVariable("path") String path, Cart cart, @PathVariable("tables-path") String tablesPath, Model model) {
        if (account != null) {
            model.addAttribute(account);
        }
        Store store = storeRepository.findStoreWithSetmenuListAndMenuListByPath(path);
        model.addAttribute(store);

        Tables tables = tablesRepository.findTablesWithAccountAndEventByStoreAndTablesPath(store, tablesPath);//TODO n+1성능
        model.addAttribute("tables", tables);
        List<Orders> ordersList = ordersRepository.findOrdersWithSetmenuListAndMenuListAndRequestOrderListByTablesAndOrderStatusTypeIsNot(tables, OrderStatusType.COMPLETE_PAYMENT);
        model.addAttribute("orderList", ordersList);
//        List<Setmenu> setmenuList = setmenuRepository.findSetmenuWithMenuListByStore(store);
//        List<Setmenu> setmenuList = store.getSetmenuList();
        model.addAttribute("setmenuList", store.getSetmenuList());
        model.addAttribute("menuList", store.getMenuList());
        model.addAttribute("cart", cart);

        String qrCode = "https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=http://localhost:8080/store/" + path + "/tables/" + tablesPath;
        model.addAttribute("qrcode", qrCode);
        return "tables/view";
    }

    /*테이블 장바구니 - 세트메뉴 추가*/
    @GetMapping("/{tables-path}/cart/add-setmenu")
    public String addSetmenuOfCart(@CurrentAccount Account account, @RequestParam("id") String id, @PathVariable("path") String path, Cart cart, @PathVariable("tables-path") String tablesPath, Model model) {
        Setmenu setmenu = setmenuRepository.findById(Long.valueOf(id)).orElseThrow();

        cart.getOrders().getSetMenuList().add(setmenu);

        return "redirect:/store/" + URLEncoder.encode(path, StandardCharsets.UTF_8) + "/tables/" + URLEncoder.encode(tablesPath, StandardCharsets.UTF_8);
    }

    /*테이블 장바구니 - 세트메뉴 제거*/
    @GetMapping("/{tables-path}/cart/remove-setmenu")
    public String removeSetmenuOfCart(@CurrentAccount Account account, @RequestParam("id") String id, @PathVariable("path") String path, Cart cart, @PathVariable("tables-path") String tablesPath, Model model) {
        Optional<Setmenu> setmenu = setmenuRepository.findById(Long.valueOf(id));
        cart.getOrders().getSetMenuList().remove(setmenu.orElseThrow());

        return "redirect:/store/" + URLEncoder.encode(path, StandardCharsets.UTF_8) + "/tables/" + URLEncoder.encode(tablesPath, StandardCharsets.UTF_8);
    }

    /*테이블 장바구니 - 메뉴 추가*/
    @GetMapping("/{tables-path}/cart/add-menu")
    public String addMenuOfCart(@CurrentAccount Account account, @RequestParam("id") String id, @PathVariable("path") String path, Cart cart, @PathVariable("tables-path") String tablesPath, Model model) {
        Optional<Menu> menu = menuRepository.findById(Long.valueOf(id));
        cart.getOrders().getMenuList().add(menu.orElseThrow());

        return "redirect:/store/" + URLEncoder.encode(path, StandardCharsets.UTF_8) + "/tables/" + URLEncoder.encode(tablesPath, StandardCharsets.UTF_8);
    }

    /*테이블 장바구니 - 메뉴 제거*/
    @GetMapping("/{tables-path}/cart/remove-menu")
    public String removeMenuOfCart(@CurrentAccount Account account, @RequestParam("id") String id, @PathVariable("path") String path, Cart cart, @PathVariable("tables-path") String tablesPath, Model model) {
        Optional<Menu> menu = menuRepository.findById(Long.valueOf(id));
        cart.getOrders().getMenuList().remove(menu.orElseThrow());

        return "redirect:/store/" + URLEncoder.encode(path, StandardCharsets.UTF_8) + "/tables/" + URLEncoder.encode(tablesPath, StandardCharsets.UTF_8);
    }

    /*테이블 장바구니 - 요청사항 추가*/
    @PostMapping("/{tables-path}/cart/add-request")
    public String addRequestOrder(@CurrentAccount Account account, @RequestParam String request, Cart cart, @PathVariable("path") String path, @PathVariable("tables-path") String tablesPath, Model model) {
        RequestOrder requestOrder = tablesService.addRequestOrder(RequestOrder.builder().content(request).build());

        cart.getOrders().getRequestOrderList().add(requestOrder);

        return "redirect:/store/" + URLEncoder.encode(path, StandardCharsets.UTF_8) + "/tables/" + URLEncoder.encode(tablesPath, StandardCharsets.UTF_8);
    }

    /*테이블 장바구니 - 요청사항 삭제*/
    @GetMapping("/{tables-path}/cart/remove-request")
    public String removeRequestOrder(@CurrentAccount Account account, @RequestParam String id, Cart cart, @PathVariable("path") String path, @PathVariable("tables-path") String tablesPath, Model model) {
        Optional<RequestOrder> byId = requestOrderRepository.findById(Long.valueOf(id));
        RequestOrder requestOrder = byId.orElseThrow();
        cart.getOrders().getRequestOrderList().remove(requestOrder);

        tablesService.removeRequestOrder(requestOrder);

        return "redirect:/store/" + URLEncoder.encode(path, StandardCharsets.UTF_8) + "/tables/" + URLEncoder.encode(tablesPath, StandardCharsets.UTF_8);
    }

    /*테이블 주문 - 추가*/
    @PostMapping("/{tables-path}/add-orders")
    public String addOrders(@CurrentAccount Account account, @PathVariable("path") String path, Cart cart, @PathVariable("tables-path") String tablesPath, Model model, SessionStatus sessionStatus) {
        Store store = storeRepository.findStoreWithKeywordsAndWaitingListAndFavoritesListByPath(path);
        Tables tables = tablesRepository.findByStoreAndTablesPath(store, tablesPath);
        tablesService.addOrders(store, tables, cart);

        sessionStatus.setComplete();
        cart = new Cart();
        return "redirect:/store/" + URLEncoder.encode(path, StandardCharsets.UTF_8) + "/tables/" + URLEncoder.encode(tablesPath, StandardCharsets.UTF_8);
    }

    /*테이블 주문 - 제거*/
    @GetMapping("/{tables-path}/remove-orders")
    public String removeOrders(@CurrentAccount Account account, @PathVariable("path") String path, @RequestParam("id") String id, @PathVariable("tables-path") String tablesPath, Model model) {
        Store store = storeRepository.findStoreWithKeywordsAndWaitingListAndFavoritesListByPath(path);

        Tables tables = tablesRepository.findByStoreAndTablesPath(store, tablesPath);

        Optional<Orders> orders = ordersRepository.findById(Long.valueOf(id));
        tablesService.removeOrders(store, tables, orders.orElseThrow());

        return "redirect:/store/" + URLEncoder.encode(path, StandardCharsets.UTF_8) + "/tables/" + URLEncoder.encode(tablesPath, StandardCharsets.UTF_8);
    }

    /*테이블 착석 - 요청*/
    @PostMapping("/{tables-path}/sit-down")
    public String sitDownRequest(@CurrentAccount Account account, @PathVariable("path") String path, @PathVariable("tables-path") String tablesPath, Model model) {
        Store store = storeRepository.findStoreByPath(path);
        Tables tables = tablesRepository.findByStoreAndTablesPath(store, tablesPath);
        tablesService.sitDownRequest(tables, account);
        return "redirect:/store/" + URLEncoder.encode(path, StandardCharsets.UTF_8) + "/tables/" + URLEncoder.encode(tablesPath, StandardCharsets.UTF_8);
    }

    /*테이블 착석 - 요청 취소*/
    @PostMapping("/{tables-path}/sit-up")
    public String sitUpRequest(@CurrentAccount Account account, @PathVariable("path") String path, @PathVariable("tables-path") String tablesPath, Model model) {
        Store store = storeRepository.findStoreByPath(path);
        Tables tables = tablesRepository.findByStoreAndTablesPath(store, tablesPath);

        Event event = eventRepository.findByTablesAndAccount(tables, account);
        tablesService.sitUpRequest(tables, event);

        return "redirect:/store/" + URLEncoder.encode(path, StandardCharsets.UTF_8) + "/tables/" + URLEncoder.encode(tablesPath, StandardCharsets.UTF_8);
    }

    /*테이블 착석 - 수락*/
    @GetMapping("/{tables-path}/sit-accept")
    public String sitAccept(@CurrentAccount Account account, @RequestParam String id, @PathVariable("path") String path, @PathVariable("tables-path") String tablesPath, Model model) {
        Store store = storeRepository.findStoreByPath(path);
        Tables tables = tablesRepository.findByStoreAndTablesPath(store, tablesPath);

        Optional<Account> byId = accountRepository.findById(Long.valueOf(id));
        Event event = eventRepository.findByTablesAndAccount(tables, byId.orElseThrow());
        tablesService.sitAccept(tables, event);

        return "redirect:/store/" + URLEncoder.encode(path, StandardCharsets.UTF_8) + "/tables/" + URLEncoder.encode(tablesPath, StandardCharsets.UTF_8);
    }

    /*테이블 착석 - 수락 취소*/
    @GetMapping("/{tables-path}/sit-accept-cancel")
    public String sitAcceptCancel(@CurrentAccount Account account, @RequestParam String id, @PathVariable("path") String path, @PathVariable("tables-path") String tablesPath, Model model) {
        Store store = storeRepository.findStoreByPath(path);
        Tables tables = tablesRepository.findByStoreAndTablesPath(store, tablesPath);

        Optional<Account> byId = accountRepository.findById(Long.valueOf(id));
        Event event = eventRepository.findByTablesAndAccount(tables, byId.orElseThrow());
        tablesService.sitAcceptCancel(tables, event);

        return "redirect:/store/" + URLEncoder.encode(path, StandardCharsets.UTF_8) + "/tables/" + URLEncoder.encode(tablesPath, StandardCharsets.UTF_8);
    }

    /*테이블 결제요청*/
    @PostMapping("/{tables-path}/request-payment")
    public String requestPayment(@CurrentAccount Account account, @PathVariable("path") String path, @PathVariable("tables-path") String tablesPath, Model model) {
        Store store = storeRepository.findStoreByPath(path);
        Tables tables = tablesRepository.findByStoreAndTablesPath(store, tablesPath);

        tablesService.requestPayment(tables);

        return "redirect:/store/" + URLEncoder.encode(path, StandardCharsets.UTF_8) + "/tables/" + URLEncoder.encode(tablesPath, StandardCharsets.UTF_8);
    }

    /*테이블 결제요청 취소*/
    @PostMapping("/{tables-path}/cancel-request-payment")
    public String cancelRequestPayment(@CurrentAccount Account account, @PathVariable("path") String path, @PathVariable("tables-path") String tablesPath, Model model) {
        Store store = storeRepository.findStoreByPath(path);
        Tables tables = tablesRepository.findByStoreAndTablesPath(store, tablesPath);

        tablesService.cancelRequestPayment(tables);

        return "redirect:/store/" + URLEncoder.encode(path, StandardCharsets.UTF_8) + "/tables/" + URLEncoder.encode(tablesPath, StandardCharsets.UTF_8);
    }

    /*테이블 결제 - 중간*/
    @PostMapping("/{tables-path}/complete-payment-middle")
    public String completePaymentMiddle(@CurrentAccount Account account, @PathVariable("path") String path, @PathVariable("tables-path") String tablesPath, Model model) {
        Store store = storeRepository.findStoreByPath(path);
        Tables tables = tablesRepository.findByStoreAndTablesPath(store, tablesPath);

        tablesService.completePaymentMiddle(tables);

        return "redirect:/store/"+ URLEncoder.encode(path, StandardCharsets.UTF_8)+"/tables/"+URLEncoder.encode(tablesPath, StandardCharsets.UTF_8);
    }

    /*테이블 결제 - 최종*/
    @PostMapping("/{tables-path}/complete-payment")
    public String completePayment(@CurrentAccount Account account, @PathVariable("path") String path, @PathVariable("tables-path") String tablesPath, Model model) {
        Store store = storeRepository.findStoreByPath(path);
        Tables tables = tablesRepository.findByStoreAndTablesPath(store, tablesPath);

        tablesService.completePayment(tables);

        return "redirect:/store/"+ URLEncoder.encode(path, StandardCharsets.UTF_8)+"/tables/"+URLEncoder.encode(tablesPath, StandardCharsets.UTF_8);
    }

    /*테이블 주문목록 - 뷰*/
    @GetMapping("/{tables-path}/management")
    public String tableManagementView(@CurrentAccount Account account, @PathVariable("path") String path, @PathVariable("tables-path") String tablesPath, Model model) {
        System.out.println("log");
        model.addAttribute(account);
        Store store = storeRepository.findStoreByPath(path);
        model.addAttribute(store);
        System.out.println("log2");
        Tables tables = tablesRepository.findByStoreAndTablesPath(store, tablesPath);
        model.addAttribute(tables);
        System.out.println("log3");
        List<Orders> ordersList = ordersRepository.findOrdersWithSetmenuListAndMenuListAndRequestOrderListByTables(tables);
        putCategorizedOrders(model, ordersList);
        System.out.println("log3");
        return "tables/management";
    }

    private void putCategorizedOrders(Model model, List<Orders> ordersList) {
        List<Orders> newOrderList = new ArrayList<>();
        List<Orders> confirmOrderList = new ArrayList<>();
        List<Orders> beforeCompleteList = new ArrayList<>();
        List<Orders> afterCompleteList = new ArrayList<>();
        List<Orders> completePaymentList = new ArrayList<>();

        for (var orders: ordersList) {
            confirmOrderList.add(orders);
            switch (orders.getOrderStatusType()) {
                case NEW_ORDER: newOrderList.add(orders); confirmOrderList.remove(orders); break;
                case BEFORE_COMPLETE: beforeCompleteList.add(orders); break;
                case AFTER_COMPLETE: afterCompleteList.add(orders); break;
                case COMPLETE_PAYMENT: completePaymentList.add(orders); break;
            }
        }

        model.addAttribute("ordersList", ordersList);
        model.addAttribute("newOrderList", newOrderList);
        model.addAttribute("confirmOrderList", confirmOrderList);
        model.addAttribute("beforeCompleteList", beforeCompleteList);
        model.addAttribute("afterCompleteList", afterCompleteList);
        model.addAttribute("completePaymentList", completePaymentList);
    }

    @GetMapping("/{tables-path}/management/confirm-orders")
    public String confirmOrders(@CurrentAccount Account account, @PathVariable("path") String path, @PathVariable("tables-path") String tablesPath, @RequestParam String id, Model model) {
        Orders orders = ordersRepository.findById(Long.valueOf(id)).orElseThrow();
        tablesService.confirmOrders(orders);
        return "redirect:/store/"+ URLEncoder.encode(path, StandardCharsets.UTF_8)+"/tables/"+URLEncoder.encode(tablesPath, StandardCharsets.UTF_8)+"/management";
    }

    @GetMapping("/{tables-path}/management/after-complete")
    public String afterComplete(@CurrentAccount Account account, @PathVariable("path") String path, @PathVariable("tables-path") String tablesPath, @RequestParam String id, Model model) {
        Orders orders = ordersRepository.findById(Long.valueOf(id)).orElseThrow();
        tablesService.afterComplete(orders);
        return "redirect:/store/"+ URLEncoder.encode(path, StandardCharsets.UTF_8)+"/tables/"+URLEncoder.encode(tablesPath, StandardCharsets.UTF_8)+"/management";
    }
}
