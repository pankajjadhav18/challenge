package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.FundTransferDto;
import com.dws.challenge.exception.AccountValidationException;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;

  @Getter
  private final EmailNotificationService emailNotificationService;
  
  @Autowired
  public AccountsService(AccountsRepository accountsRepository, EmailNotificationService emailNotificationService) {
    this.accountsRepository = accountsRepository;
    this.emailNotificationService = emailNotificationService;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }
  
	public void fundTransfer(FundTransferDto fundTransferDto) {
		FundTransferDto fundTransferResDto = null;
		try {
			fundTransferResDto = this.accountsRepository.fundTransfer(fundTransferDto);
		} catch (AccountValidationException ave) {
			throw ave;
		} catch (Exception e) {
			throw e;
		}
		if (fundTransferResDto != null) {
			Account fromAccount = this.accountsRepository.getAccount(fundTransferResDto.getFromAccountId());
			Account toAccount = this.accountsRepository.getAccount(fundTransferResDto.getToAccountId());
			String fromAccNotificationDesc = "Amount " + fundTransferDto.getAmount()
					+ "successfully transfer to account " + fundTransferDto.getToAccountId();
			String toAccNotificationDesc = "Amount " + fundTransferDto.getAmount()
					+ "successfully recieved from account " + fundTransferDto.getFromAccountId();
			this.emailNotificationService.notifyAboutTransfer(fromAccount, fromAccNotificationDesc);
			this.emailNotificationService.notifyAboutTransfer(toAccount, toAccNotificationDesc);
		}

	}
}
