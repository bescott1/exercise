package com.ippon.exercise.service;

import com.ippon.exercise.domain.Customer;
import com.ippon.exercise.domain.dto.CustomerDTO;
import com.ippon.exercise.domain.exception.CustomerNotFound;
import com.ippon.exercise.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class BankAccountService {

    private CustomerRepository customerRepository;

    public BankAccountService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerDTO createCustomer(CustomerDTO customerDTO) {

        Customer customer = new Customer();
        customer.setBalance(0.0);
        customer.setFirstName(customerDTO.getFirstName());
        customer.setLastName(customerDTO.getLastName());
        customer.setDateAdded(Instant.now());

        Customer save = customerRepository.save(customer);

        customerDTO.setId(save.getId());
        customerDTO.setBalance(save.getBalance());
        return customerDTO;
    }

    @Transactional
    public void deposit(long customerId, double deposit) {
        Customer customer = getCustomer(customerId);
        customer.setBalance(customer.getBalance() + deposit);
        customerRepository.save(customer);
    }

    @Transactional
    public CustomerDTO getBalanceForCustomer(long customerId) {
        Customer customer = getCustomer(customerId);

        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setBalance(customer.getBalance());
        customerDTO.setId(customer.getId());
        customerDTO.setFirstName(customer.getFirstName());
        customerDTO.setLastName(customer.getLastName());

        return customerDTO;
    }

    private Customer getCustomer(long customerId) {
        return customerRepository
                .findById(customerId)
                .orElseThrow(CustomerNotFound::new);
    }

}
