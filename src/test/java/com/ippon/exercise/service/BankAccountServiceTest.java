package com.ippon.exercise.service;

import com.ippon.exercise.domain.Customer;
import com.ippon.exercise.domain.dto.CustomerDTO;
import com.ippon.exercise.domain.exception.CustomerNotFound;
import com.ippon.exercise.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Captor
    public ArgumentCaptor<Customer> customerCapture;

    @InjectMocks
    public BankAccountService subject;

    @Test
    void createCustomerTest() {

        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setFirstName("Ben");
        customerDTO.setLastName("Scott");

        Customer customer = createCustomer();

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDTO result = subject.createCustomer(customerDTO);

        verify(customerRepository).save(customerCapture.capture());
        assertThat(result.getId(), is(1000L));
        assertThat(result.getBalance(), is(0.0));
        assertThat(customerCapture.getValue().getDateAdded(), is(notNullValue()));

    }

    @Test
    void deposit_customerNotFound_throwsCustomerNotFound() {
        when(customerRepository.findById(1001L)).thenReturn(Optional.empty());
        assertThrows(CustomerNotFound.class, () -> subject.deposit(1001L, 20.30));
    }

    @Test
    void deposit_customerFound_changesBalanceOfCustomer() {
        when(customerRepository.findById(1001L)).thenReturn(Optional.of(createCustomer()));
        subject.deposit(1001L, 20.30);

        verify(customerRepository).save(customerCapture.capture());

        assertThat(customerCapture.getValue().getBalance(), is(20.30));
    }

    @Test
    void getBalanceForCustomer_customerNotFound_throwsCustomerNotFound() {
        when(customerRepository.findById(1001L)).thenReturn(Optional.empty());
        assertThrows(CustomerNotFound.class, () -> subject.getBalanceForCustomer(1001L));
    }

    @Test
    void getBalanceForCustomer_customerFound_returnsCustomer() {
        Customer customer = createCustomer();
        customer.setBalance(10.30);
        when(customerRepository.findById(1001L)).thenReturn(Optional.of(customer));
        CustomerDTO result = subject.getBalanceForCustomer(1001L);

        assertThat(result.getId(), is(1000L));
        assertThat(result.getBalance(), is(10.30));
        assertThat(result.getFirstName(), is("Ben"));
        assertThat(result.getLastName(), is("Scott"));
    }

    private Customer createCustomer() {
        Customer customer = new Customer();
        customer.setBalance(0.0);
        customer.setLastName("Scott");
        customer.setFirstName("Ben");
        customer.setId(1000L);
        return customer;
    }
}
