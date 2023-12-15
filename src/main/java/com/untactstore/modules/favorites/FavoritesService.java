package com.untactstore.modules.favorites;

import com.untactstore.modules.account.Account;
import com.untactstore.modules.store.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FavoritesService {
    private final FavoritesRepository favoritesRepository;

    public void addFavorites(Account account, Store store) {
        if (store.getFavoritesList().stream().map(Favorites::getAccount).filter(a -> a.equals(account)).collect(Collectors.toList()).isEmpty()) {
            Favorites favorites = new Favorites();
            favorites.setAccount(account);

            store.addFavorites(favorites);

            favoritesRepository.save(favorites);
        }
    }

    public void removeFavorites(Account account, Store store) {
        Favorites favorites = favoritesRepository.findByAccountAndStore(account, store);
        store.removeFavorites(favorites);
        favoritesRepository.delete(favorites);
    }
}
