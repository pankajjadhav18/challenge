package com.dws.challenge.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.FundTransferDto;
import com.dws.challenge.exception.AccountValidationException;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.service.AccountsService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {

  private final AccountsService accountsService;

  @Autowired
  public AccountsController(AccountsService accountsService) {
    this.accountsService = accountsService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
    log.info("Creating account {}", account);

    try {
    this.accountsService.createAccount(account);
    } catch (DuplicateAccountIdException daie) {
      return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping(path = "/{accountId}")
  public Account getAccount(@PathVariable String accountId) {
    log.info("Retrieving account for id {}", accountId);
    return this.accountsService.getAccount(accountId);
  }

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/fundtransfer")
	public ResponseEntity<Object> fundTransfer(@RequestBody @Valid FundTransferDto fundTransferDto) {
		log.info("transfer {} from {} to {}", fundTransferDto.getAmount(), fundTransferDto.getFromAccountId(),
				fundTransferDto.getToAccountId());
		try {
			this.accountsService.fundTransfer(fundTransferDto);
		} catch (AccountValidationException acve) {
			return new ResponseEntity<>(acve.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<>("Something went wrong!!", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
	}
  
}
