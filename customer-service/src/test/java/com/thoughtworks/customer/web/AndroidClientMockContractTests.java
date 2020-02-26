package com.thoughtworks.customer.web;

import au.com.dius.pact.provider.junit.State;
import com.thoughtworks.customer.data.Customer;
import com.thoughtworks.customer.repository.CustomerRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class AndroidClientMockContractTests extends AbstractAndroidClientContractTests {

    @MockBean
    private CustomerRepository repository;

    @State("an existing customer with a valid id")
    void pactWithAnExistingCustomer() {
        when(repository.findById(1234L))
                .thenReturn(Mono.just(new Customer(1234L, "Test", "First")));
    }

    @State("a non-existent customer with an invalid id")
    void pactWithANonExistentCustomer() {
        when(repository.findById(anyLong())).thenReturn(Mono.empty());
    }
}
