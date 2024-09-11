package com.dws.challenge.domain;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FundTransferDto {
	@NotNull
	@NotEmpty
	private final String fromAccountId;

	@NotNull
	@NotEmpty
	private final String toAccountId;

	@NotNull
	@Min(value = 1, message = "Transfer amount must be positive.")
	private BigDecimal amount;

	@JsonCreator
	public FundTransferDto(@JsonProperty("fromAccountId") String fromAccountId,
			@JsonProperty("toAccountId") String toAccountId, @JsonProperty("amount") BigDecimal amount) {
		this.fromAccountId = fromAccountId;
		this.toAccountId = toAccountId;
		this.amount = amount;
	}

	
	
	

}
