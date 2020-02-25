package com.thoughtworks.customer.repository;

import com.thoughtworks.customer.data.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {
}
