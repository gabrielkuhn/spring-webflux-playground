package com.gabrielkuhn.webfluxplayground.controller;

import com.gabrielkuhn.webfluxplayground.domain.Customer;
import com.gabrielkuhn.webfluxplayground.service.CustomerService;
import com.gabrielkuhn.webfluxplayground.util.CustomerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
class CustomerControllerTest {
    @InjectMocks
    private CustomerController customerController;

    @Mock
    private CustomerService customerService;

    private final Customer customer = CustomerFactory.get();

    @BeforeEach
    public void setUp() {
        BDDMockito.when(customerService.findAll())
                .thenReturn(Flux.just(customer));

        BDDMockito.when(customerService.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.just(customer));

        BDDMockito.when(customerService.save(CustomerFactory.builder().build()))
                .thenReturn(Mono.just(customer));

        BDDMockito.when(customerService.delete(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());

        BDDMockito.when(customerService.update(customer))
                .thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("findAll returns a flux of customer")
    public void findAll_ReturnsFluxOfCustomers_WhenSuccessful() {
        StepVerifier.create(customerController.findAll())
                .expectSubscription()
                .expectNext(customer)
                .verifyComplete();
    }

    @Test
    @DisplayName("findById returns a mono with a customer when it exists")
    public void findById_ReturnsMonoCustomer_WhenSuccessful() {
        StepVerifier.create(customerController.findById(1))
                .expectSubscription()
                .expectNext(customer)
                .verifyComplete();
    }

    @Test
    @DisplayName("findById returns a mono error when the customer does not exists")
    public void findById_ReturnsMonoError_WhenEmptyMonoIsReturned() {
        BDDMockito.when(customerService.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.error(new RuntimeException()));

        StepVerifier.create(customerController.findById(1))
                .expectSubscription()
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("save creates an customer when successful")
    public void save_CreatesCustomer_WhenSuccessful() {
        Customer customerToBeSaved = CustomerFactory.builder().build();

        StepVerifier.create(customerController.save(customerToBeSaved))
                .expectSubscription()
                .expectNext(customer)
                .verifyComplete();
    }

    @Test
    @DisplayName("delete removes the customer when successful")
    public void delete_RemovesCustomer_WhenSuccessful() {
        StepVerifier.create(customerController.delete(1))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    @DisplayName("update saves the updated customer and returns an empty mono when successful")
    public void update_SaveUpdatedCustomer_WhenSuccessful() {
        BDDMockito.when(customerService.save(customer))
                .thenReturn(Mono.just(customer));

        StepVerifier.create(customerController.update(1, customer))
                .expectSubscription()
                .verifyComplete();
    }
}