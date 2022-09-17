package com.gabrielkuhn.webfluxplayground.integration;

import com.gabrielkuhn.webfluxplayground.domain.Customer;
import com.gabrielkuhn.webfluxplayground.repository.CustomerRepository;
import com.gabrielkuhn.webfluxplayground.util.CustomerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class CustomerControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer;

    @BeforeEach
    public void setUp() {
        customerRepository.deleteAll().subscribe();

        customerRepository.save(CustomerFactory.builder().build())
                .subscribe(createdCustomer -> customer = createdCustomer);
    }

    @Test
    @DisplayName("findAll returns all customers when successful")
    public void findAll_ReturnsCustomers_WhenSuccessful() {
        webTestClient.get()
                .uri("/customers")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Customer.class)
                .hasSize(1)
                .contains(customer);
    }

    @Test
    @DisplayName("findById returns a customer when it exists")
    public void findById_ReturnsCustomer_WhenSuccessful() {
        webTestClient.get()
                .uri("/customers/" + customer.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Customer.class)
                .isEqualTo(customer);
    }

    @Test
    @DisplayName("findById returns an error when the customer does not exists")
    public void findById_ReturnsMonoError_WhenCustomerDoesNotExists() {
        webTestClient.get()
                .uri("/customers/" + Integer.MAX_VALUE)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("Customer not found");
    }

    @Test
    @DisplayName("save creates an customer when successful")
    public void save_CreatesCustomer_WhenSuccessful() {
        Customer customerToBeSaved = CustomerFactory.builder().build();

        webTestClient.post()
                .uri("/customers")
                .body(BodyInserters.fromValue(customerToBeSaved))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Customer.class)
                .isEqualTo(customer.withId(customer.getId() + 1));
    }

    @Test
    @DisplayName("save returns an error with bad request when name is empty")
    public void save_ReturnsError_WhenNameIsEmpty() {
        Customer customerToBeSaved = Customer.builder().build();

        webTestClient.post()
                .uri("/customers")
                .body(BodyInserters.fromValue(customerToBeSaved))
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(400);
    }

    @Test
    @DisplayName("delete removes the customer when successful")
    public void delete_RemovesCustomer_WhenSuccessful() {
        webTestClient.delete()
                .uri("/customers/" + customer.getId())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        StepVerifier.create(customerRepository.count())
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    @DisplayName("delete returns an error when the customer does not exists")
    public void delete_ReturnsMonoError_WhenEmptyMonoIsReturned() {
        webTestClient.delete()
                .uri("/customers/" + Integer.MAX_VALUE)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("Customer not found");
    }

    @Test
    @DisplayName("update saves the updated customer and returns an empty body when successful")
    public void update_SaveUpdatedCustomer_WhenSuccessful() {
        Customer newCustomer = Customer.builder()
                .name("new name")
                .build();

        webTestClient.put()
                .uri("/customers/" + customer.getId())
                .body(BodyInserters.fromValue(newCustomer))
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        StepVerifier.create(customerRepository.findById(customer.getId()))
                .expectNext(newCustomer.withId(customer.getId()))
                .verifyComplete();
    }

    @Test
    @DisplayName("update returns an error when the customer does not exists")
    public void update_ReturnsMonoError_WhenEmptyMonoIsReturned() {
        webTestClient.put()
                .uri("/customers/" + Integer.MAX_VALUE)
                .body(BodyInserters.fromValue(CustomerFactory.get()))
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("Customer not found");

        StepVerifier.create(customerRepository.findById(customer.getId()))
                .expectNext(customer)
                .verifyComplete();
    }
}
