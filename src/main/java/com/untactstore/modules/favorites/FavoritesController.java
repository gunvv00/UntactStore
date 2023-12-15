package com.untactstore.modules.favorites;

import com.untactstore.modules.account.Account;
import com.untactstore.modules.account.authentication.CurrentAccount;
import com.untactstore.modules.store.Store;
import com.untactstore.modules.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/store/{path}")
@RequiredArgsConstructor
public class FavoritesController {
//    private final FavoritesRepository favoritesRepository;
    private final FavoritesService favoritesService;
    private final StoreRepository storeRepository;

    @GetMapping("/addFavorites")
    public String addFavorites(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Store store = storeRepository.findStoreByPath(path);

        if (store.getFavoritesList().stream().filter(f -> f.getAccount().equals(account)).collect(Collectors.toList()).isEmpty()) {
            favoritesService.addFavorites(account, store);
        }

        return "redirect:/store/"+ URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

    @GetMapping("/removeFavorites")
    public String removeFavorites(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Store store = storeRepository.findStoreByPath(path);

        if (!store.getFavoritesList().stream().filter(f -> f.getAccount().equals(account)).collect(Collectors.toList()).isEmpty()) {
            favoritesService.removeFavorites(account, store);
        }

        return "redirect:/store/"+ URLEncoder.encode(path, StandardCharsets.UTF_8);
    }
}
