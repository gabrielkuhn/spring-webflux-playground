package com.gabrielkuhn.webfluxplayground.service;

import com.gabrielkuhn.webfluxplayground.domain.Customer;
import com.gabrielkuhn.webfluxplayground.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Flux<Customer> findAll() {
        return customerRepository.findAll();
    }

    public Mono<Customer> findById(Integer id) {
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found")));
    }

    public Mono<Customer> save(Customer customer) {
        return customerRepository.save(customer);
    }

    public Mono<Void> update(Customer customer) {
        return findById(customer.getId())
                .flatMap(foundCustomer -> customerRepository.save(customer))
                .then();
    }

    public Mono<Void> delete(Integer id) {
        return findById(id)
                .flatMap(customerRepository::delete);
    }
}
