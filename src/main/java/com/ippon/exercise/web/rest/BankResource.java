package com.ippon.exercise.web.rest;

import com.ippon.exercise.domain.dto.CustomerDTO;
import com.ippon.exercise.domain.dto.DepositDTO;
import com.ippon.exercise.service.BankAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/bank")
public class BankResource {

    private BankAccountService bankAccountService;

    public BankResource(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping("/customer")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDTO createCustomer(@Valid @RequestBody CustomerDTO newCustomer) {

        CustomerDTO customer = bankAccountService.createCustomer(newCustomer);
        return customer;
    }

    @GetMapping("/{customerId}")
    public CustomerDTO getBalanceForCustomer(@PathVariable(name = "customerId") long customerId) {
        return bankAccountService.getBalanceForCustomer(customerId);
    }

    @PostMapping("/deposit")
    public void deposit(@Valid @RequestBody DepositDTO depositDTO) {
        bankAccountService.deposit(depositDTO.getCustomerId(), depositDTO.getDeposit());
    }


}
