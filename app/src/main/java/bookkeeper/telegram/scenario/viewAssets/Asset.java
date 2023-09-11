package bookkeeper.telegram.scenario.viewAssets;

import bookkeeper.entity.Account;

import java.math.BigDecimal;

class Asset {
    private final Account account;
    private final BigDecimal balance;

    Asset(Account account, BigDecimal balance) {
        this.account = account;
        this.balance = balance;
    }

    Account getAccount() {
        return account;
    }

    BigDecimal getBalance() {
        return balance;
    }
}
