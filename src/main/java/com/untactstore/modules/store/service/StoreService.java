package com.untactstore.modules.store.service;

import com.untactstore.modules.account.Account;
import com.untactstore.modules.account.repository.AccountRepository;
import com.untactstore.modules.account.service.AccountService;
import com.untactstore.modules.address.Address;
import com.untactstore.modules.address.AddressForm;
import com.untactstore.modules.address.AddressRepository;
import com.untactstore.modules.grade.Grade;
import com.untactstore.modules.grade.GradeRepository;
import com.untactstore.modules.keyword.Keyword;
import com.untactstore.modules.keyword.KeywordRepository;
import com.untactstore.modules.menu.Menu;
import com.untactstore.modules.menu.Setmenu;
import com.untactstore.modules.menu.form.MenuForm;
import com.untactstore.modules.menu.form.SetmenuForm;
import com.untactstore.modules.menu.repository.MenuRepository;
import com.untactstore.modules.menu.repository.SetmenuRepository;
import com.untactstore.modules.order.OrdersRepository;
import com.untactstore.modules.review.*;
import com.untactstore.modules.store.Store;
import com.untactstore.modules.store.event.StoreCreateEvent;
import com.untactstore.modules.store.form.StoreForm;
import com.untactstore.modules.store.form.StoreProfileForm;
import com.untactstore.modules.store.form.StoreSettingsForm;
import com.untactstore.modules.store.repository.StoreRepository;
import com.untactstore.modules.table.EventRepository;
import com.untactstore.modules.table.Tables;
import com.untactstore.modules.table.TablesForm;
import com.untactstore.modules.table.TablesRepository;
import com.untactstore.modules.waiting.WaitingRepository;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final ModelMapper modelMapper;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final MenuRepository menuRepository;
    private final SetmenuRepository setmenuRepository;
    private final TablesRepository tablesRepository;
    private final ReviewRepository reviewRepository;
    private final KeywordRepository keywordRepository;
    private final ReplyRepository replyRepository;
    private final WaitingRepository waitingRepository;
    private final EventRepository eventRepository;
    private final OrdersRepository ordersRepository;
    private final AddressRepository addressRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final GradeRepository gradeRepository;


    public Store newStore(Account account, StoreForm storeForm) {
        Account updateAccount = accountRepository.findById(account.getId()).orElseThrow();

        Store store = modelMapper.map(storeForm, Store.class);
        store.setOwner(updateAccount);
        store.setUseBanner(false);

        Address address = addressRepository.save(new Address());
        address.setStore(store);
        store.setAddress(address);

        accountService.login(updateAccount); //현재 로그인된 정보 업데이트

        Store newStore = storeRepository.save(store);
        return newStore;
    }

    /*상점 프로필 - 수정*/
    public void updateProfile(Store store, StoreProfileForm storeProfileForm) {
        modelMapper.map(storeProfileForm, store);
        storeRepository.save(store);
    }

    /*상점 이미지 - 추가*/
    public void updateStoreImage(Store store, String image) {
        store.setImage(image);
    }

    /*상점 이미지 - 사용*/
    public void enableStoreBanner(Store store) {
        store.setUseBanner(true);
    }

    /*상점 이미지 - 사용 안함*/
    public void disableStoreBanner(Store store) {
        store.setUseBanner(false);
    }

    /*메뉴 - 메뉴 추가*/
    public void addMenu(Store store, MenuForm menuForm) {
        Menu newMenu = modelMapper.map(menuForm, Menu.class);
        store.addMenu(newMenu);

        Menu menu = menuRepository.save(newMenu);
    }

    /*메뉴 - 메뉴 삭제*/
    public void removeMenu(Store store, Menu menu) {
        store.removeMenu(menu);
        menuRepository.delete(menu);
    }

    /*메뉴 - 세트메뉴 추가*/
    public void addSetmenu(Store store, SetmenuForm setmenuForm) {
        List<Menu> newMenus = setmenuForm.getMenuList();
        menuRepository.saveAll(newMenus);

        setmenuForm.getMenuList().stream().forEach(menu -> {
            setmenuForm.setTotalPrice(setmenuForm.getTotalPrice() + menu.getPrice());
        });

//        setmenuForm.getMenuList().stream().forEach(m -> );

        Setmenu newSetmenu = modelMapper.map(setmenuForm, Setmenu.class);
        newSetmenu.setStore(store);

//        store.addSetmenu(newSetmenu);

        Setmenu setmenu = setmenuRepository.save(newSetmenu);
    }
    /*메뉴 - 세트메뉴 삭제*/
    public void removeSetmenu(Store store, Setmenu setmenu) {
        setmenu.getMenuList().stream().forEach(menu -> menuRepository.delete(menu));
        store.removeSetmenu(setmenu);
        setmenuRepository.delete(setmenu);
    }

    /*테이블 - 새 테이블*/
    public void newTable(Account account, Store store, TablesForm tablesForm) {
        Tables tables = Tables.builder()
                .tableNum(store.getTableList().size()+1)
                .tablesPath(store.getTableList().size()+1+"")
                .account(null)
                .store(store)
                .personnel(tablesForm.getPersonnel())
                .build();
        store.addTable(tables);
        tablesRepository.save(tables);
    }

    /*테이블 - 테이블 삭제*/
    public void removeTable(Store store) {
        Tables remove = tablesRepository.findFirstByStoreOrderByTableNumDesc(store);
        store.removeTable(remove);
        eventRepository.deleteAllByTables(remove);
        ordersRepository.deleteAllByTables(remove);
        tablesRepository.delete(remove);
    }

    /*테이블 - 테이블 수정*/
    public void updateTable(TablesForm tablesForm) {
        Optional<Tables> byId = tablesRepository.findById(tablesForm.getId());
        byId.ifPresent(t -> t.setPersonnel(tablesForm.getPersonnel()));
    }

    /*키워드 - 상점에 키워드 추가*/
    public void addKeyword(Store store, Keyword keyword) {
        Optional<Store> byId = storeRepository.findById(store.getId());
        byId.ifPresent(a -> a.getKeywords().add(keyword));
        eventPublisher.publishEvent(new StoreCreateEvent(store));
    }

    /*키워드 - 상에서 키워드 삭제*/
    public void removeKeyword(Store store, Keyword keyword) {
        Optional<Store> byId = storeRepository.findById(store.getId());
        byId.ifPresent(a -> a.getKeywords().remove(keyword));
    }

    /*상점 주소 수정*/
    public void updateAddress(Store store, AddressForm addressForm) {
        Address address = addressRepository.findByStore(store);
        modelMapper.map(addressForm, address);
    }

    /*사업자등록번호 - 수정*/
    public void updateLicensee(Store store, StoreSettingsForm storeSettingsForm) {
        modelMapper.map(storeSettingsForm, store);
    }

    /*리뷰 등록*/
    public void addReview(Account account, Store store, ReviewForm reviewForm) {
        Review review = modelMapper.map(reviewForm, Review.class);
        review.setAccount(account);
        review.setStore(store);
        review.setReviewAt(LocalDateTime.now());
        reviewRepository.save(review);
    }

    /*답글 등록*/
    public void addReply(Account account, Store store, ReplyForm replyForm) {
        Optional<Review> byId = reviewRepository.findById(replyForm.getReview_id());

        Reply reply = modelMapper.map(replyForm, Reply.class);
        reply.setAccount(account);
        reply.setStore(store);
        reply.setReview(byId.orElseThrow());
        reply.setReplyAt(LocalDateTime.now());
        replyRepository.save(reply);
    }

    public void openStore(Store store) {
        store.setOpen(true);
    }

    public void closeStore(Store store) {
        store.setOpen(false);
    }

    public void fullStore(Store store) {
        store.setWaiting(true);
    }

    public void UnfilledStore(Store store) {
        store.setWaiting(false);
    }

    /*테스트 데이터*/
    public void generateTestStoreDate(Account account) {
        for (int i = 0; i < 30; i++) {
            String randomvalue = RandomString.make(5);
            StoreForm store = new StoreForm();
            store.setName("테스트 상점 " + randomvalue);
            store.setPath("test-" + randomvalue);
            store.setShortDescription("테스트용 상점 입니다.");
            store.setFullDescription("테스트용 상점 입니다.");
            store.setLicensee("테스트용 사업자 등록번호");

            Store newStore = this.newStore(account, store);
            Keyword keyword = keywordRepository.findByName("부리또");
            newStore.getKeywords().add(keyword);
        }
    }

    public void updateMenuImage(Menu menu, String image) {
        menu.setImage(image);
    }

    public void setGrade(Account account, Store store, String grade) {
        if (store.getGradeList().stream().filter(g->g.getAccount().equals(account)).collect(Collectors.toList()).isEmpty()) {
            Grade newGrade = new Grade();
            newGrade.setAccount(account);
            newGrade.setGrade(Integer.valueOf(grade));
            store.addGrade(newGrade);
            gradeRepository.save(newGrade);
        } else {
            Grade updateGrade = gradeRepository.findByAccountAndStore(account, store);
            updateGrade.setGrade(Integer.valueOf(grade));
        }

        double totalGrade = 0;
        List<Integer> collect = store.getGradeList().stream().map(Grade::getGrade).collect(Collectors.toList());
        for (Integer g:collect) {
            totalGrade += g;
        }
        store.setGrade(totalGrade/collect.size());
    }

    //TODO 리뷰 답글 삭제
}
