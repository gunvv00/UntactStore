package com.untactstore.modules.main;

import com.untactstore.modules.account.Account;
import com.untactstore.modules.account.authentication.CurrentAccount;
import com.untactstore.modules.account.repository.AccountRepository;
import com.untactstore.modules.favorites.Favorites;
import com.untactstore.modules.favorites.FavoritesRepository;
import com.untactstore.modules.keyword.KeywordRepository;
import com.untactstore.modules.location.Location;
import com.untactstore.modules.store.Store;
import com.untactstore.modules.store.repository.StoreRepository;
import com.untactstore.modules.table.Tables;
import com.untactstore.modules.table.TablesRepository;
import com.untactstore.modules.waiting.Waiting;
import com.untactstore.modules.waiting.WaitingForm;
import com.untactstore.modules.waiting.WaitingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final AccountRepository accountRepository;
    private final StoreRepository storeRepository;
    private final TablesRepository tablesRepository;
    private final WaitingRepository waitingRepository;
    private final KeywordRepository keywordRepository;
    private final FavoritesRepository favoritesRepository;

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model,
                       @PageableDefault(size=9,sort="grade",direction= Sort.Direction.DESC) Pageable pageable) {
        if (account != null) {
            System.out.println("log-account");
            Account accountLoaded = accountRepository.findAccountWithKeywordsAndLocationsById(account.getId());
            model.addAttribute(accountLoaded);

            System.out.println("log-store");
//            내가 등록한 키워드 리스트
            List<Store> myKeywordStoreList = storeRepository.findStoreWithKeywordByOwner(accountLoaded.getKeywords());
            model.addAttribute("myKeywordStoreList", myKeywordStoreList);

            System.out.println("log-myfavorites");
//            내가 등록한 지역 리스트
            List<List<Store>> listOfMyLocationStoreList = storeRepository.findStoreWithAddressByOwner(accountLoaded.getLocations());
            model.addAttribute("listOfMyLocationStoreList", listOfMyLocationStoreList);
            List<Location> locations = accountLoaded.getLocations().stream().collect(Collectors.toList());
            model.addAttribute("locationList", locations);

            System.out.println("log-myLocation");
//            내즐겨찾기 리스트
            List<Favorites> favorites = favoritesRepository.findByAccount(accountLoaded);
            List<Store> myFavoritesList = storeRepository.findStoreByFavoritesList(favorites);
            model.addAttribute("myFavoritesList", myFavoritesList);

            System.out.println("log-favorites-store");
//            즐겨찾기순 리스트
            List<Store> favoritesList = storeRepository.findFirst5ByOrderByFavoritesCountDesc();
            model.addAttribute("favoritesList", favoritesList);

            System.out.println("log-grade-store");
//            평점순 리스트
            List<Store> gradeList = storeRepository.findFirst5ByOrderByGradeDesc();
            model.addAttribute("gradeList", gradeList);

            System.out.println("log-waitingList");
//            대기표
            List<Waiting> waitingList = waitingRepository.findAllByAccountAndAttendedOrderByTurnAscWaitingAtAsc(account, false);
            model.addAttribute("waitingList", waitingList);

            System.out.println("log-myTableList");
//            이용중인 테이블
            List<Tables> tablesList = tablesRepository.findByAccount(accountLoaded);
            model.addAttribute("tablesList", tablesList);

            System.out.println("log-myStore");
//            내 상점 목록
            List<Store> myStoreList = storeRepository.findFirst5AllByOwner(accountLoaded);
            model.addAttribute("myStoreList", myStoreList);

//            모든 상점
//            Pageable pageable = PageRequest.of(0,9, Sort.by("grade").descending());
            Page<Store> storePage = storeRepository.findAll(pageable);
            model.addAttribute("storePage", storePage);
            return "index-after-login";
        }

        Page<Store> storePage = storeRepository.findAll(pageable);
        model.addAttribute("storePage", storePage);

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /*대기표*/
    @GetMapping("/my-waiting")
    public String waitingView(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);

        List<Waiting> waitingAll = waitingRepository.findAllByAccount(account);
//        List<Waiting> waitingList = waitingRepository.findAllByAccountAndAttended(account, false);

        List<Waiting> waitingList = new ArrayList<>();
        List<Waiting> endList = new ArrayList<>();
        for (Waiting waiting: waitingAll) {
            if (waiting.isAttended()) {
                endList.add(waiting);
            } else {
                waitingList.add(waiting);
            }
        }

        model.addAttribute("waitingList", waitingList);
        model.addAttribute("endList", endList);

        model.addAttribute("waitingForm", new WaitingForm());
        return "waiting/my-waiting";
    }

    /*검색*/
    @GetMapping("/search/store")
    public String searchStore(@CurrentAccount Account account, @RequestParam String keyword, Model model,
                              @PageableDefault(size=9,sort="grade",direction= Sort.Direction.DESC) Pageable pageable) {
        if (account != null) {
            model.addAttribute(account);
        }

        Page<Store> storePage = storeRepository.findByKeyword(keyword, pageable);
        model.addAttribute("storePage", storePage);

        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty",
                pageable.getSort().toString().contains("grade")?"grade":"favoritesCount");
        return "search";
    }
}
