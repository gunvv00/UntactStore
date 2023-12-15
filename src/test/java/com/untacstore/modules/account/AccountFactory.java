package com.untacstore.modules.account;

import com.untactstore.modules.account.Account;
import com.untactstore.modules.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountFactory {

    @Autowired
    AccountRepository accountRepository;

    public Account createAccount(String nickname) {
        Account byungryang = new Account();
        byungryang.setUsername(nickname);
        byungryang.setEmail(nickname + "@email.com");
        accountRepository.save(byungryang);
        return byungryang;
    }

}
