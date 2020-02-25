package com.thoughtworks.customer;

import com.thoughtworks.customer.data.Customer;
import com.thoughtworks.customer.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class CustomerServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }

    @Bean
    @Profile("test")
    public CommandLineRunner initDb(CustomerRepository repository) {
        return args -> {
            Flux.just(1234L)
                    .map(id -> new Customer(id, "Test", "First"))
                    .flatMap(repository::save)
                    .doOnNext(c -> log.info("Saving customer with id '{}'", c.getId()))
                    .blockLast();

                    repository.findAll()
                    .doOnNext(c -> log.info("Found customer with id: '{}' and name: '{} {}'",
                            c.getId(), c.getFirstName(), c.getLastName()))
                    .blockLast();
        };
    }
}
