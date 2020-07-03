package com.ippon.exercise.domain.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class DepositDTO {

    @NotNull
    private Long customerId;

    @NotNull
    @Min(value = 0)
    private Double deposit;

    public DepositDTO() {
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Double getDeposit() {
        return deposit;
    }

    public void setDeposit(Double deposit) {
        this.deposit = deposit;
    }
}
