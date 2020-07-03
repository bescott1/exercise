package com.ippon.exercise.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ippon.exercise.domain.dto.CustomerDTO;
import com.ippon.exercise.domain.dto.DepositDTO;
import com.ippon.exercise.domain.exception.CustomerNotFound;
import com.ippon.exercise.service.BankAccountService;
import com.ippon.exercise.web.rest.errors.ExceptionTranslator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(classes = {BankResource.class, ExceptionTranslator.class})
class BankResourceIT {

    @MockBean
    private BankAccountService bankAccountService;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private ObjectMapper objectMapper= new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    public void before() {
        BankResource subject = new BankResource(bankAccountService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(subject)
            .setControllerAdvice(exceptionTranslator)
            .build();
    }

    @Test
    public void customerCreate_missingFirstName_returns400() throws Exception {
        CustomerDTO request = new CustomerDTO();
        request.setLastName("Last");
        mockMvc
            .perform(post("/api/bank/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.parameters.fieldErrors[0].field", equalTo("firstName")))
            .andExpect(jsonPath("$.parameters.fieldErrors[0].message", equalTo("NotEmpty")));
    }

    @Test
    public void customerCreate_missingLastName_returns400() throws Exception {
        CustomerDTO request = new CustomerDTO();
        request.setFirstName("First");
        mockMvc
            .perform(post("/api/bank/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.parameters.fieldErrors[0].field", equalTo("lastName")))
            .andExpect(jsonPath("$.parameters.fieldErrors[0].message", equalTo("NotEmpty")));
    }

    @Test
    public void customerCreate_success_returns202WithIdAndZeroBalance() throws Exception {
        CustomerDTO request = new CustomerDTO();
        request.setLastName("Last");
        request.setFirstName("First");

        CustomerDTO response = new CustomerDTO();
        response.setFirstName(request.getFirstName());
        response.setLastName(request.getLastName());
        response.setId(1000L);
        response.setBalance(0.0);

        when(bankAccountService.createCustomer(any(CustomerDTO.class))).thenReturn(response);

        mockMvc
            .perform(post("/api/bank/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.firstName", equalTo("First")))
            .andExpect(jsonPath("$.lastName", equalTo("Last")))
            .andExpect(jsonPath("$.id", equalTo(1000)))
            .andExpect(jsonPath("$.balance", equalTo(0.0)));
    }

    @Test
    public void getCustomer_notFound_returns404() throws Exception {
        when(bankAccountService.getBalanceForCustomer(1001))
            .thenThrow(new CustomerNotFound());

        mockMvc
            .perform(get("/api/bank/1001"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.parameters.message", equalTo("Customer not found")));
    }

    @Test
    public void getCustomer_Ben_returnsCustomerDetails() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setLastName("Scott");
        customerDTO.setFirstName("Ben");
        customerDTO.setId(1001L);
        customerDTO.setBalance(100.30);

        when(bankAccountService.getBalanceForCustomer(1001L)).thenReturn(customerDTO);

        mockMvc
            .perform(get("/api/bank/1001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", equalTo("Ben")))
            .andExpect(jsonPath("$.lastName", equalTo("Scott")))
            .andExpect(jsonPath("$.id", equalTo(1001)))
            .andExpect(jsonPath("$.balance", equalTo(100.30)));
    }

    @Test
    public void deposit_noCustomerId_returns400() throws Exception {
        DepositDTO request = new DepositDTO();
        request.setDeposit(3.50);
        mockMvc
            .perform(post("/api/bank/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.parameters.fieldErrors[0].field", equalTo("customerId")))
            .andExpect(jsonPath("$.parameters.fieldErrors[0].message", equalTo("NotNull")));
    }

    @Test
    public void deposit_noDepositAmount_returns400() throws Exception {
        DepositDTO request = new DepositDTO();
        request.setCustomerId(350L);
        mockMvc
            .perform(post("/api/bank/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.parameters.fieldErrors[0].field", equalTo("deposit")))
            .andExpect(jsonPath("$.parameters.fieldErrors[0].message", equalTo("NotNull")));
    }

    @Test
    public void deposit_negativeDeposit_returns400() throws Exception {
        DepositDTO request = new DepositDTO();
        request.setCustomerId(350L);
        request.setDeposit(-3.50);
        mockMvc
            .perform(post("/api/bank/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.parameters.fieldErrors[0].field", equalTo("deposit")))
            .andExpect(jsonPath("$.parameters.fieldErrors[0].message", equalTo("Min")));
    }

    @Test
    public void deposit_customerNotFound_returns404() throws Exception {
        DepositDTO request = new DepositDTO();
        request.setCustomerId(350L);
        request.setDeposit(3.50);

        when(bankAccountService.getBalanceForCustomer(request.getCustomerId()))
            .thenThrow(new CustomerNotFound());

        mockMvc
            .perform(post("/api/bank/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @Test
    public void deposit_customerFound_returns200() throws Exception {
        DepositDTO request = new DepositDTO();
        request.setCustomerId(350L);
        request.setDeposit(3.50);

        mockMvc
            .perform(post("/api/bank/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }
}
