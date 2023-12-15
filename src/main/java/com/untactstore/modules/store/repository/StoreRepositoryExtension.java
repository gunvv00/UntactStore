package com.untactstore.modules.store.repository;

import com.untactstore.modules.favorites.Favorites;
import com.untactstore.modules.keyword.Keyword;
import com.untactstore.modules.location.Location;
import com.untactstore.modules.store.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public interface StoreRepositoryExtension {
    Page<Store> findByKeyword(String keyword, Pageable pageable);

    List<Store> findStoreWithKeywordByOwner(Set<Keyword> keywords);

    List<Store> findStoreByFavoritesList(List<Favorites> favorites);

    List<List<Store>> findStoreWithAddressByOwner(Set<Location> locations);
}
