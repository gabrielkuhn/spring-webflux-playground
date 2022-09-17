package com.gabrielkuhn.webfluxplayground.util;

import com.gabrielkuhn.webfluxplayground.domain.Customer;

public class CustomerFactory {
    public static Customer.CustomerBuilder builder() {
        return Customer.builder()
                .name("bob");
    }

    public static Customer get() {
        return Customer.builder()
                .id(1)
                .name("bob")
                .build();
    }
}
