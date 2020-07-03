package com.ippon.exercise.repository;

import com.ippon.exercise.ExerciseApp;
import com.ippon.exercise.domain.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest(classes = {ExerciseApp.class})
class CustomerRepositoryIT {

    @Autowired
    CustomerRepository customerRepository;

    @Test
    @Sql(scripts = "classpath:sql/bank_customer_insert.sql")
    public void exampleJPATest() {

        Optional<Customer> byId = customerRepository.findById(1001L);

        assertThat(byId.isPresent(), is(true));
        assertThat(byId.get().getBalance(), is(145.32));
    }
}
