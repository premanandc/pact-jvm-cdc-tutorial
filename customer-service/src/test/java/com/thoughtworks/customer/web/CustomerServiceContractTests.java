package com.thoughtworks.customer.web;

import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import com.thoughtworks.customer.data.Customer;
import com.thoughtworks.customer.repository.CustomerRepository;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = DEFINED_PORT)
@Provider("CustomerService")
@PactBroker(host = "localhost", port = "80")
public class CustomerServiceContractTests {

    @Autowired
    private CustomerRepository repository;

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State("A customer with an existing ID")
    void pactWithAnExistingCustomer() {
        Flux.just(1234L)
                .map(id -> new Customer(id, "Test", "First"))
                .flatMap(repository::save)
                .blockLast();
    }
}


