package com.thoughtworks.customer.web;

import au.com.dius.pact.provider.junit.State;
import com.thoughtworks.customer.data.Customer;
import com.thoughtworks.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;

public class AndroidClientIntegrationContractTests extends AbstractAndroidClientContractTests {

    @Autowired
    private CustomerRepository repository;

    @State("an existing customer with a valid id")
    void pactWithAnExistingCustomer() {
        Flux.just(1234L)
                .map(id -> new Customer(id, "Test", "First"))
                .flatMap(repository::save)
                .blockLast();
    }

    @State("a non-existent customer with an invalid id")
    void pactWithANonExistentCustomer() {

    }
}


