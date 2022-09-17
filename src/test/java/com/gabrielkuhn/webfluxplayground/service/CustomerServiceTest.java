package com.gabrielkuhn.webfluxplayground.service;

import com.gabrielkuhn.webfluxplayground.domain.Customer;
import com.gabrielkuhn.webfluxplayground.repository.CustomerRepository;
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
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    private final Customer customer = CustomerFactory.get();

    @BeforeEach
    public void setUp() {
        BDDMockito.when(customerRepository.findAll())
                .thenReturn(Flux.just(customer));

        BDDMockito.when(customerRepository.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.just(customer));

        BDDMockito.when(customerRepository.save(CustomerFactory.builder().build()))
                .thenReturn(Mono.just(customer));

        BDDMockito.when(customerRepository.delete(customer))
                .thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("findAll returns a flux of customer")
    public void findAll_ReturnsFluxOfCustomers_WhenSuccessful() {
        StepVerifier.create(customerService.findAll())
                .expectSubscription()
                .expectNext(customer)
                .verifyComplete();
    }

    @Test
    @DisplayName("findById returns a mono with a customer when it exists")
    public void findById_ReturnsMonoCustomer_WhenSuccessful() {
        StepVerifier.create(customerService.findById(1))
                .expectSubscription()
                .expectNext(customer)
                .verifyComplete();
    }

    @Test
    @DisplayName("findById returns a mono error when the customer does not exists")
    public void findById_ReturnsMonoError_WhenEmptyMonoIsReturned() {
        BDDMockito.when(customerRepository.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());

        StepVerifier.create(customerService.findById(1))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    @DisplayName("save creates an customer when successful")
    public void save_CreatesCustomer_WhenSuccessful() {
        Customer customerToBeSaved = CustomerFactory.builder().build();

        StepVerifier.create(customerService.save(customerToBeSaved))
                .expectSubscription()
                .expectNext(customer)
                .verifyComplete();
    }

    @Test
    @DisplayName("delete removes the customer when successful")
    public void delete_RemovesCustomer_WhenSuccessful() {
        StepVerifier.create(customerService.delete(1))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    @DisplayName("delete returns a mono error when the customer does not exists")
    public void delete_ReturnsMonoError_WhenEmptyMonoIsReturned() {
        BDDMockito.when(customerRepository.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());

        StepVerifier.create(customerService.delete(1))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    @DisplayName("update saves the updated customer and returns an empty mono when successful")
    public void update_SaveUpdatedCustomer_WhenSuccessful() {
        BDDMockito.when(customerRepository.save(customer))
                .thenReturn(Mono.just(customer));

        StepVerifier.create(customerService.update(customer))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    @DisplayName("update returns a mono error when the customer does not exists")
    public void update_ReturnsMonoError_WhenEmptyMonoIsReturned() {
        BDDMockito.when(customerRepository.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());

        StepVerifier.create(customerService.update(customer))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }
}