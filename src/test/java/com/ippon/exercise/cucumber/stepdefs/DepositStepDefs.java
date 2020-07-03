package com.ippon.exercise.cucumber.stepdefs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ippon.exercise.domain.dto.CustomerDTO;
import com.ippon.exercise.domain.dto.DepositDTO;
import com.ippon.exercise.web.rest.BankResource;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class DepositStepDefs extends StepDefs {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper= new ObjectMapper();

    private CustomerDTO activeCustomer;

    @Autowired
    private BankResource bankResource;

    @Before
    public void before() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(bankResource)
            .build();
    }

    @Given("a customer with first name {string} and last name {string} is created")
    public void when_customer_is_created(String firstName, String lastName) throws Exception {
        CustomerDTO request = new CustomerDTO();
        request.setFirstName(firstName);
        request.setLastName(lastName);

        String contentAsString = mockMvc
            .perform(post("/api/bank/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        CustomerDTO response = objectMapper.readValue(contentAsString, CustomerDTO.class);

        assertThat(response.getBalance(), is(0.0));
        assertThat(response.getFirstName(), is(firstName));
        assertThat(response.getLastName(), is(lastName));

        activeCustomer = response;
    }


    @When("the customer deposits {double} into his account")
    public void theCustomerDepositsIntoHisAccount(double deposit) throws Exception {
        DepositDTO request = new DepositDTO();
        request.setCustomerId(activeCustomer.getId());
        request.setDeposit(deposit);

        mockMvc
            .perform(post("/api/bank/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @Then("the customer balance is {double}")
    public void theCustomerBalanceIs(double balance) throws Exception {

        String contentAsString = mockMvc
            .perform(get("/api/bank/" + activeCustomer.getId()))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        CustomerDTO response = objectMapper.readValue(contentAsString, CustomerDTO.class);

        assertThat(response.getBalance(), is(balance));
        assertThat(response.getFirstName(), is(activeCustomer.getFirstName()));
        assertThat(response.getLastName(), is(activeCustomer.getLastName()));
    }
}
