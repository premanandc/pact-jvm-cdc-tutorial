package com.thoughtworks.customer.web;

import com.thoughtworks.customer.data.Customer;
import com.thoughtworks.customer.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(SpringExtension.class)
@WebFluxTest(CustomerController.class)
class CustomerControllerTests {

    @MockBean
    private CustomerRepository repository;

    @Autowired
    private WebTestClient client;

    @Test
    void whenFound() {
        Customer testCustomer = Customer.builder()
                .id(1234L).isNew(false).firstName("Test").lastName("First").build();
        when(repository.findById(1234L)).thenReturn(Mono.just(testCustomer));

        client.get()
                .uri("/customers/{id}", 1234L)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(1234L)
                .jsonPath("$.firstName").isEqualTo("Test")
                .jsonPath("$.lastName").isEqualTo("First");
    }

    @Test
    void whenNotFound() {
        when(repository.findById(1234L)).thenReturn(Mono.empty());

        client.get()
                .uri("/customers/{id}", 1234L)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }
}