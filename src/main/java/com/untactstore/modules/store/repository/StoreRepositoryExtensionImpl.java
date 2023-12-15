package com.untactstore.modules.store.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import com.untactstore.modules.account.QAccount;
import com.untactstore.modules.address.QAddress;
import com.untactstore.modules.favorites.Favorites;
import com.untactstore.modules.favorites.QFavorites;
import com.untactstore.modules.keyword.Keyword;
import com.untactstore.modules.keyword.QKeyword;
import com.untactstore.modules.location.Location;
import com.untactstore.modules.store.QStore;
import com.untactstore.modules.store.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StoreRepositoryExtensionImpl extends QuerydslRepositorySupport implements StoreRepositoryExtension{

    public StoreRepositoryExtensionImpl() {
        super(Store.class);
    }

    @Override
    public Page<Store> findByKeyword(String keyword, Pageable pageable) {
        QStore store = QStore.store;
        JPQLQuery<Store> query = from(store).where(store.name.containsIgnoreCase(keyword)
                .or(store.keywords.any().name.containsIgnoreCase(keyword)))
                .leftJoin(store.keywords, QKeyword.keyword).fetchJoin()
                .leftJoin(store.owner, QAccount.account).fetchJoin()
                .distinct();
        JPQLQuery<Store> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Store> fetchResults = pageableQuery.fetchResults();
        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
    }

    @Override
    public List<Store> findStoreWithKeywordByOwner(Set<Keyword> keywords) {
        QStore store = QStore.store;
        JPQLQuery<Store> query = from(store).where(store.keywords.any().in(keywords))
                .leftJoin(store.keywords, QKeyword.keyword).fetchJoin()
                .orderBy(store.grade.asc())
                .distinct()
                .limit(9);
        return query.fetch();
    }

    @Override
    public List<Store> findStoreByFavoritesList(List<Favorites> favorites) {
        QStore store = QStore.store;
        JPQLQuery<Store> query = from(store).where(store.favoritesList.any().in(favorites))
                .leftJoin(store.favoritesList, QFavorites.favorites).fetchJoin()
                .orderBy(store.grade.asc())
                .distinct()
                .limit(5);
        return query.fetch();
    }

    @Override
    public List<List<Store>> findStoreWithAddressByOwner(Set<Location> locations) {
        List<List<Store>> storeList = new ArrayList<>();

        QStore store = QStore.store;
        JPQLQuery<Store> query;
        for (Location location: locations) {
            query = from(store).where(store.address.jibunAddress.containsIgnoreCase(location.getName())
                    .or(store.address.roadAddress.containsIgnoreCase(location.getName())
                    .or(store.address.detailAddress.containsIgnoreCase(location.getName())
                    .or(store.address.extraAddress.containsIgnoreCase(location.getName())))))
                    .leftJoin(store.address, QAddress.address).fetchJoin()
                    .distinct().limit(9);
            List<Store> fetch = query.fetch();
            storeList.add(fetch);
        }
        return storeList;
    }
}
