package com.fetch.receiptprocessor.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest

public class ReceiptValidationTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void missingRetailerFieldTest() {
        Receipt receipt = Receipt.builder()
                .purchaseDate(LocalDate.parse("2022-01-02"))
                .purchaseTime(LocalTime.parse("08:13"))
                .total("2.65")
                .items(Arrays.asList(Item.builder().shortDescription("Pepsi - 12-oz").price("1.25").build()))
                .build();

        Set<ConstraintViolation<Receipt>> violations = validator.validate(receipt);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Retailer name cannot be blank");
    }

    @Test
    void missingPurchaseDateFieldTest() {
        Receipt receipt = Receipt.builder()
                .retailer("Walgreens")
                .purchaseTime(LocalTime.parse("08:13"))
                .total("2.65")
                .items(Arrays.asList(Item.builder().shortDescription("Pepsi - 12-oz").price("1.25").build()))
                .build();

        Set<ConstraintViolation<Receipt>> violations = validator.validate(receipt);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Purchase date cannot be blank");
    }

    @Test
    void missingPurchaseTimeFieldTest() {
        Receipt receipt = Receipt.builder()
                .retailer("Walgreens")
                .purchaseDate(LocalDate.parse("2022-01-02"))
                .total("2.65")
                .items(Arrays.asList(Item.builder().shortDescription("Pepsi - 12-oz").price("1.25").build()))
                .build();

        Set<ConstraintViolation<Receipt>> violations = validator.validate(receipt);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Purchase time cannot be blank");
    }

    @Test
    void missingTotalFieldTest() {
        Receipt receipt = Receipt.builder()
                .retailer("Walgreens")
                .purchaseDate(LocalDate.parse("2022-01-02"))
                .purchaseTime(LocalTime.parse("08:13"))
                .items(Arrays.asList(Item.builder().shortDescription("Pepsi - 12-oz").price("1.25").build()))
                .build();

        Set<ConstraintViolation<Receipt>> violations = validator.validate(receipt);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Total amount cannot be blank");
    }

    @Test
    void emptyItemListTest() {
        Receipt receipt = Receipt.builder()
                .retailer("Walgreens")
                .purchaseDate(LocalDate.parse("2022-01-02"))
                .purchaseTime(LocalTime.parse("08:13"))
                .total("2.65")
                .items(Collections.emptyList())  // Empty item list
                .build();

        Set<ConstraintViolation<Receipt>> violations = validator.validate(receipt);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("At least one item is required");
    }

    @Test
    void invalidTotalFormatTest() throws IOException{
        Receipt receipt = Receipt.builder()
                .retailer("Walgreens")
                .purchaseDate(LocalDate.parse("2022-01-02"))
                .purchaseTime(LocalTime.parse("08:13"))
                .total("2.6")  // Invalid format
                .items(Arrays.asList(Item.builder().shortDescription("Pepsi - 12-oz").price("1.25").build()))
                .build();

        Set<ConstraintViolation<Receipt>> violations = validator.validate(receipt);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Receipt total amount must be in the format X.XX");
    }

    @Test
    void invalidRetailerNameTest() throws IOException {
        Receipt receipt = Receipt.builder()
                .retailer("*Invalid#Name")
                .purchaseDate(LocalDate.parse("2022-01-02"))
                .purchaseTime(LocalTime.parse("08:13"))
                .total("2.65")
                .items(Collections.singletonList(
                        Item.builder().shortDescription("Pepsi - 12-oz").price("1.25").build()
                ))
                .build();

        Set<ConstraintViolation<Receipt>> violations = validator.validate(receipt);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Retailer name contains invalid characters");
    }
}
