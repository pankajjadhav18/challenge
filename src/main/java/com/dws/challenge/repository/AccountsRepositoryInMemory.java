package com.dws.challenge.repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.FundTransferDto;
import com.dws.challenge.exception.AccountValidationException;
import com.dws.challenge.exception.DuplicateAccountIdException;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class AccountsRepositoryInMemory implements AccountsRepository {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    @Override
    public void createAccount(Account account) throws DuplicateAccountIdException {
        Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
        if (previousAccount != null) {
            throw new DuplicateAccountIdException(
                    "Account id " + account.getAccountId() + " already exists!");
        }
    }

    @Override
    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    @Override
    public void clearAccounts() {
        accounts.clear();
    }

    @Override
	public FundTransferDto fundTransfer(FundTransferDto fundTransferDto) {
		log.debug("Before = {}", accounts);
		accounts.compute(fundTransferDto.getFromAccountId(), (key, oldValueFromAcc) -> {
			BigDecimal remainingBal = oldValueFromAcc.getBalance().subtract(fundTransferDto.getAmount());
			if (remainingBal.compareTo(BigDecimal.ZERO) < 0) {
				throw new AccountValidationException(
						"Insufficient Balance in account " + fundTransferDto.getFromAccountId());
			} else {
				accounts.compute(fundTransferDto.getToAccountId(), (keyToAcc, oldValueToAcc) -> {
					BigDecimal addedBal = oldValueToAcc.getBalance();
					try {
						addedBal = oldValueToAcc.getBalance().add(fundTransferDto.getAmount());						
					}catch (Exception e) {
						oldValueToAcc.setBalance(oldValueToAcc.getBalance());
						throw e;
					}
					oldValueToAcc.setBalance(addedBal);
					return oldValueToAcc;
				});
			}
			oldValueFromAcc.setBalance(remainingBal);
			return oldValueFromAcc;
		});
		log.debug("After = {}", accounts);
		return fundTransferDto;
	}
    
}
