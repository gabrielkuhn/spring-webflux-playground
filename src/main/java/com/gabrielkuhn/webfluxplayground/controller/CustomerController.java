package com.gabrielkuhn.webfluxplayground.controller;

import com.gabrielkuhn.webfluxplayground.domain.Customer;
import com.gabrielkuhn.webfluxplayground.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public Flux<Customer> findAll() {
        return customerService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Customer> findById(@PathVariable Integer id) {
        return customerService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Customer> save(@Valid @RequestBody Customer customer) {
        return customerService.save(customer);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> update(@PathVariable Integer id, @Valid @RequestBody Customer customer) {
        return customerService.update(customer.withId(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Integer id) {
        return customerService.delete(id);
    }
}
