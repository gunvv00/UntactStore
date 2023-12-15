package com.untactstore.modules.store.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.untactstore.modules.account.Account;
import com.untactstore.modules.account.authentication.CurrentAccount;
import com.untactstore.modules.address.Address;
import com.untactstore.modules.address.AddressRepository;
import com.untactstore.modules.keyword.Keyword;
import com.untactstore.modules.keyword.KeywordForm;
import com.untactstore.modules.keyword.KeywordRepository;
import com.untactstore.modules.keyword.KeywordService;
import com.untactstore.modules.location.LocationRepository;
import com.untactstore.modules.location.LocationService;
import com.untactstore.modules.menu.Menu;
import com.untactstore.modules.menu.service.MenuService;
import com.untactstore.modules.menu.Setmenu;
import com.untactstore.modules.menu.form.MenuForm;
import com.untactstore.modules.menu.form.SetmenuForm;
import com.untactstore.modules.menu.repository.MenuRepository;
import com.untactstore.modules.menu.repository.SetmenuRepository;
import com.untactstore.modules.store.Store;
import com.untactstore.modules.address.AddressForm;
import com.untactstore.modules.store.form.StoreSettingsForm;
import com.untactstore.modules.store.form.StoreProfileForm;
import com.untactstore.modules.store.repository.StoreRepository;
import com.untactstore.modules.store.service.StoreService;
import com.untactstore.modules.table.TablesForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@Transactional
@RequiredArgsConstructor
@SessionAttributes("setmenuForm")
@RequestMapping("/store/{path}/settings")
public class StoreSettingsController {
    private final StoreRepository storeRepository;
    private final StoreService storeService;
    private final ModelMapper modelMapper;
    private final MenuService menuService;
    private final MenuRepository menuRepository;
    private final SetmenuRepository setmenuRepository;
    private final KeywordRepository keywordRepository;
    private final AddressRepository addressRepository;
    private final KeywordService keywordService;
    private final LocationRepository locationRepository;
    private final LocationService locationService;
    private final ObjectMapper objectMapper;

    /*상점 setting 프로필 - 폼*/
    @GetMapping("/profile")
    public String storeProfileForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        model.addAttribute(account);

        Store store = storeRepository.findStoreWithKeywordsAndWaitingListAndFavoritesListByPath(path);
        model.addAttribute(store);
        StoreProfileForm storeProfileForm = modelMapper.map(store, StoreProfileForm.class);
        model.addAttribute("storeProfileForm", storeProfileForm);

        return "store/settings/profile";
    }

    /*상점 setting 프로필 - 수정*/
    @PostMapping("/profile")
    public String updateStoreProfile(@CurrentAccount Account account, @PathVariable String path, @Valid StoreProfileForm storeProfileForm, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "store/settings/profile";
        }

        Store store = storeRepository.findStoreByPath(path);
        storeService.updateProfile(store, storeProfileForm);

        return "redirect:/store/" + URLEncoder.encode(path, StandardCharsets.UTF_8) + "/settings/profile";
    }

    /*상점 setting 배너 - 폼*/
    @GetMapping("/banner")
    public String storeImageForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Store store = storeRepository.findStoreWithKeywordsAndWaitingListAndFavoritesListByPath(path);
        model.addAttribute(account);
        model.addAttribute(store);
        return "store/settings/banner";
    }

    /*상점 setting 배너 - 수정*/
    @PostMapping("/banner")
    public String storeImageSubmit(@CurrentAccount Account account, @PathVariable String path,
                                   String image, RedirectAttributes attributes) {
        Store store = storeRepository.findStoreByPath(path);
        storeService.updateStoreImage(store, image);
        attributes.addFlashAttribute("message", "상점 이미지를 수정했습니다.");
        return "redirect:/store/" + URLEncoder.encode(path, StandardCharsets.UTF_8) + "/settings/banner";
    }

    /*상점 setting 배너 - 사용*/
    @PostMapping("/banner/enable")
    public String enableStudyBanner(@CurrentAccount Account account, @PathVariable String path) {
        Store store = storeRepository.findStoreByPath(path);
        storeService.enableStoreBanner(store);
        return "redirect:/store/" + URLEncoder.encode(path, StandardCharsets.UTF_8) + "/settings/banner";
    }

    /*상점 setting 배너 - 사용안함*/
    @PostMapping("/banner/disable")
    public String disableStudyBanner(@CurrentAccount Account account, @PathVariable String path) {
        Store store = storeRepository.findStoreByPath(path);
        storeService.disableStoreBanner(store);
        return "redirect:/store/" + URLEncoder.encode(path, StandardCharsets.UTF_8) + "/settings/banner";
    }

    /*상점 setting 메뉴 - 폼*/
    @GetMapping("/menu")
    public String storeMenuForm(@CurrentAccount Account account, SetmenuForm setmenuForm, @PathVariable String path, Model model) {
        model.addAttribute(account);

        Store store = storeRepository.findStoreWithKeywordsAndWaitingListAndFavoritesListByPath(path);
        model.addAttribute(store);
        // 세트메뉴
//        model.addAttribute("setmenuList", store.getSetmenuList()); //성능이 안좋음
        List<Setmenu> setmenuList = setmenuRepository.findSetmenuWithMenuListByStore(store);
        model.addAttribute("setmenuList", setmenuList);

        // 메뉴
        List<Menu> menuList = menuRepository.findByStore(store);
        model.addAttribute("menuList", menuList);

        model.addAttribute("menuForm", new MenuForm());

        List<Menu> menuListOfSetmenu = setmenuForm.getMenuList();
        model.addAttribute("readyMenuList", menuListOfSetmenu);

        return "store/settings/menu";
    }

    /*상점 setting 메뉴 - 이미지 수정*/
    @PostMapping("/menu/add-image")
    public String storeMenuImageSubmit(@CurrentAccount Account account, @PathVariable String path, @RequestParam String id,
                                   String image, RedirectAttributes attributes) {
        Menu menu = menuRepository.findById(Long.valueOf(id)).orElseThrow();
        storeService.updateMenuImage(menu, image);
        attributes.addFlashAttribute("message", "메뉴 이미지를 수정했습니다.");
        return "redirect:/store/" + URLEncoder.encode(path, StandardCharsets.UTF_8) + "/settings/banner";
    }

    /*상점 setting 메뉴 - 메뉴 추가*/
    @PostMapping("/menu")
    public String addMenu(@CurrentAccount Account account,@Valid MenuForm menuForm, Errors errors, @PathVariable String path, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "store/settings/menu";
        }

        Store store = storeRepository.findStoreWithKeywordsAndWaitingListAndFavoritesListByPath(path);
        storeService.addMenu(store, menuForm);
        return "redirect:/store/"+URLEncoder.encode(path, StandardCharsets.UTF_8)+"/settings/menu";
    }

    /*상점 setting 메뉴 - 메뉴 삭제*/
    @GetMapping("/remove-menu")
    public String removeMenu(@CurrentAccount Account account, @PathVariable String path, @RequestParam(name = "id") Menu menu) {
        Store store = storeRepository.findStoreWithKeywordsAndWaitingListAndFavoritesListByPath(path);
        storeService.removeMenu(store, menu);

        return "redirect:/store/"+URLEncoder.encode(path, StandardCharsets.UTF_8)+"/settings/menu";
    }

    /*상점 setting 메뉴 - 세트메뉴에 메뉴 추가*/
    @PostMapping("/ready-setmenu")
    public String addMenuOfSetmenu(@CurrentAccount Account account, @Valid MenuForm menuForm, Errors errors, SetmenuForm setmenuForm, @PathVariable String path, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "store/settings/menu";
        }

        Store store = storeRepository.findStoreWithKeywordsAndWaitingListAndFavoritesListByPath(path);

        Menu newMenu = modelMapper.map(menuForm, Menu.class);
        setmenuForm.getMenuList().add(newMenu);

//        menuService.addMenu(store, newMenu);

        model.addAttribute("setmenuForm", setmenuForm);

        return "redirect:/store/"+URLEncoder.encode(path, StandardCharsets.UTF_8)+"/settings/menu";
    }

    /*상점 setting 메뉴 - 세트메뉴에 이미지 추가*/
    @PostMapping("/image-setmenu")
    public String setImageOfSetmenu(@CurrentAccount Account account, String setmenuImage, SetmenuForm setmenuForm, @PathVariable String path, Model model) {
        setmenuForm.setImage(setmenuImage);

        model.addAttribute("setmenuForm", setmenuForm);
        return "redirect:/store/"+URLEncoder.encode(path, StandardCharsets.UTF_8)+"/settings/menu";
    }

    /*상점 setting 메뉴 - 세트메뉴에서 메뉴 제거*/
    @PostMapping("/remove-ready-setmenu")
    public String removeMenuOfSetmenu(@CurrentAccount Account account, Menu menu, SetmenuForm setmenuForm, @PathVariable String path, Model model) {
        Store store = storeRepository.findStoreWithKeywordsAndWaitingListAndFavoritesListByPath(path);

        setmenuForm.getMenuList().remove(menu);

        model.addAttribute("setmenuForm", setmenuForm);

        return "redirect:/store/"+URLEncoder.encode(path, StandardCharsets.UTF_8)+"/settings/menu";
    }

    /*상점 setting 메뉴 - 세트메뉴 추가*/
    @PostMapping("/setmenu")
    public String addSetmenu(@CurrentAccount Account account, SetmenuForm setmenuForm, @PathVariable String path, Model model, SessionStatus sessionStatus) {
        Store store = storeRepository.findStoreWithKeywordsAndWaitingListAndFavoritesListByPath(path);

        storeService.addSetmenu(store, setmenuForm);

        sessionStatus.setComplete();
        setmenuForm = new SetmenuForm();
        return "redirect:/store/"+URLEncoder.encode(path, StandardCharsets.UTF_8)+"/settings/menu";
    }

    /*상점 setting 메뉴 - 세트메뉴 삭제*/
    @GetMapping("/remove-setmenu")
    public String removeSetmenu(@CurrentAccount Account account, @PathVariable String path, @RequestParam(name = "id") Setmenu setmenu) {
        Store store = storeRepository.findStoreWithKeywordsAndWaitingListAndFavoritesListByPath(path);

        storeService.removeSetmenu(store, setmenu);
        return "redirect:/store/"+URLEncoder.encode(path, StandardCharsets.UTF_8)+"/settings/menu";
    }

    /*상점 setting 테이블 - 폼 */
    @GetMapping("/tables")
    public String storeTablesForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        model.addAttribute(account);

        Store store = storeRepository.findStoreWithKeywordsAndWaitingListAndFavoritesListByPath(path);
        model.addAttribute(store);

        model.addAttribute("tableList", store.getTableList());

        model.addAttribute("tablesForm", new TablesForm());
        return "store/settings/tables";
    }

    /*상점 setting 테이블 - 새 테이블*/
    @PostMapping("/new-table")
    public String newTable(@CurrentAccount Account account, TablesForm tablesForm, @PathVariable String path, Model model) {
        Store store = storeRepository.findStoreByPath(path);
        storeService.newTable(account, store, tablesForm);
        return "redirect:/store/"+URLEncoder.encode(path, StandardCharsets.UTF_8)+"/settings/tables";
    }

    /*상점 setting 테이블 - 테이블 삭제*/
    @PostMapping("/remove-table")
    public String removeTable(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Store store = storeRepository.findStoreByPath(path);
        storeService.removeTable(store);
        return "redirect:/store/"+URLEncoder.encode(path, StandardCharsets.UTF_8)+"/settings/tables";
    }

    /*상점 setting 테이블 - 테이블 수정*/
    @PostMapping("/update-table")
    public String updateTable(@CurrentAccount Account account, TablesForm tablesForm, @PathVariable String path, Model model) {

        storeService.updateTable(tablesForm);

        return "redirect:/store/"+URLEncoder.encode(path, StandardCharsets.UTF_8)+"/settings/tables";
    }

    /*키워드 / 주소 - 폼*/
    @GetMapping("/keywords")
    public String keyword_addressForm(@CurrentAccount Account account, @PathVariable String path, Model model) throws JsonProcessingException {
        model.addAttribute(account);

        Store store = storeRepository.findStoreWithKeywordsAndWaitingListAndFavoritesListByPath(path);
        model.addAttribute(store);

        //상점에 설정된 keyword
        Set<Keyword> keywords = store.getKeywords();
        model.addAttribute("keywords", keywords.stream().map(Keyword::getName).collect(Collectors.toList()));
        //전체 keyword
        List<String> allKeywords = keywordRepository.findAll().stream().map(Keyword::getName).collect(Collectors.toList());
        model.addAttribute("whitelistOfKeyword", objectMapper.writeValueAsString(allKeywords));

        Address address = addressRepository.findByStore(store);
        AddressForm addressForm = modelMapper.map(address, AddressForm.class);
        model.addAttribute(addressForm);

        return "store/settings/keywords";
    }

    /*키워드 - 키워드 추가*/
    @PostMapping("/keywords/add")
    @ResponseBody
    public ResponseEntity addKeyword(@CurrentAccount Account account, @PathVariable String path, @RequestBody KeywordForm keywordForm) {
        Keyword keyword = keywordService.findOrCreateNew(keywordForm.getName());
        if (keyword == null) {
            return ResponseEntity.badRequest().build();
        }

        Store store = storeRepository.findStoreWithKeywordByPath(path);

        storeService.addKeyword(store, keyword);
        return ResponseEntity.ok().build();
    }

    /*키워드 - 키워드 삭제*/
    @PostMapping("/keywords/remove")
    @ResponseBody
    public ResponseEntity removeKeyword(@CurrentAccount Account account, @PathVariable String path, @RequestBody KeywordForm keywordForm) {
        Keyword keyword = keywordService.findOrCreateNew(keywordForm.getName());
        if (keyword == null) {
            return ResponseEntity.badRequest().build();
        }

        Store store = storeRepository.findStoreWithKeywordByPath(path);

        storeService.removeKeyword(store, keyword);
        return ResponseEntity.ok().build();
    }

    /*주소 - 수정*/
    @PostMapping("/address/add")
    public String updateAddress(@CurrentAccount Account account, @PathVariable String path, AddressForm addressForm, Model model) {
        Store store = storeRepository.findStoreByPath(path);
        storeService.updateAddress(store, addressForm);
        return "redirect:/store/"+URLEncoder.encode(path, StandardCharsets.UTF_8)+"/settings/keywords";
    }

    /*상점 - 폼*/
    @GetMapping("/store")
    public String storeSettingForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        model.addAttribute(account);

        Store store = storeRepository.findStoreWithKeywordsAndWaitingListAndFavoritesListByPath(path);
        model.addAttribute(store);

        model.addAttribute("storeSettingsForm", modelMapper.map(store, StoreSettingsForm.class));
        return "store/settings/store";
    }

    /*상점 - 사업자등록번호 수정*/
    @PostMapping("/licensee")
    public String updateStore(@CurrentAccount Account account, @Valid StoreSettingsForm storeSettingsForm, Errors errors, @PathVariable String path, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "store/settings/store";
        }

        Store store = storeRepository.findStoreByPath(path);
        storeService.updateLicensee(store, storeSettingsForm);

        return "redirect:/store/"+URLEncoder.encode(path, StandardCharsets.UTF_8)+"/settings/store";
    }

    /*상점 - 오픈*/
    @GetMapping("/open")
    public String openStore(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Store store = storeRepository.findStoreByPath(path);
        storeService.openStore(store);
        
        return "redirect:/store/"+URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

    /*상점 - 클로즈*/
    @GetMapping("/close")
    public String closeStore(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Store store = storeRepository.findStoreByPath(path);
        storeService.closeStore(store);
        //TODO table 착석 요청 초기화
        return "redirect:/store/"+URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

    /*상점 - full*/
    @GetMapping("/full")
    public String fullStore(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Store store = storeRepository.findStoreByPath(path);
        storeService.fullStore(store);
        return "redirect:/store/"+URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

    /*상점 - Unfilled*/
    @GetMapping("/Unfilled")
    public String UnfilledStore(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Store store = storeRepository.findStoreByPath(path);
        storeService.UnfilledStore(store);
        return "redirect:/store/"+URLEncoder.encode(path, StandardCharsets.UTF_8);
    }
}
