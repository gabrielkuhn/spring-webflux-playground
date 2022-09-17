package com.gabrielkuhn.webfluxplayground.repository;

import com.gabrielkuhn.webfluxplayground.domain.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {

}
