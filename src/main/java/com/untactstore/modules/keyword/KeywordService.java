package com.untactstore.modules.keyword;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;

    public Keyword findOrCreateNew(String name) {
        Keyword keyword = keywordRepository.findByName(name);
        if (keyword == null) {
            keyword = keywordRepository.save(Keyword.builder().name(name).build());
            System.out.println("키워드 생성 완료");
        }
        return keyword;
    }
}
