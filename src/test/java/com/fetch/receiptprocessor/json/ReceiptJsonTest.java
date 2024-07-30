package com.fetch.receiptprocessor.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fetch.receiptprocessor.config.JacksonConfig;
import com.fetch.receiptprocessor.model.Item;
import com.fetch.receiptprocessor.model.Receipt;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Set;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@Import(JacksonConfig.class)
public class ReceiptJsonTest {

    private static final String EXPECTED_RECEIPT_JSON_PATH = "examples/expected-receipt.json";
    private static final String MISSING_RETAILER_FIELD_JSON_PATH = "examples/missing-retailer-field.json";
    private static final String MISSING_PURCHASE_DATE_FIELD_JSON_PATH = "examples/missing-purchasedate-field.json";
    private static final String MISSING_PURCHASE_TIME_FIELD_JSON_PATH = "examples/missing-purchasetime-field.json";
    private static final String MISSING_TOTAL_FIELD_JSON_PATH = "examples/missing-total-field.json";
    private static final String EMPTY_ITEM_JSON_PATH = "examples/empty-receipt-item.json";

    @Autowired
    private JacksonTester<Receipt> json;

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        try (ValidatorFactory factory = buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testLoadResource() throws IOException {
        ClassPathResource resource = new ClassPathResource(EXPECTED_RECEIPT_JSON_PATH);
        assertThat(resource.exists()).isTrue();
    }

    @Test
    void validReceiptSerializationTest() throws IOException {

        final ClassPathResource expectedReceipt = new ClassPathResource(EXPECTED_RECEIPT_JSON_PATH);

        Receipt receipt = Receipt.builder()
                .retailer("Walgreens")
                .purchaseDate(LocalDate.parse("2022-01-02"))
                .purchaseTime(LocalTime.parse("08:13"))
                .total("2.65")
                .items(Arrays.asList(
                        Item.builder().shortDescription("Pepsi - 12-oz").price("1.25").build(),
                        Item.builder().shortDescription("Dasani").price("1.40").build()))
                .build();

          assertThat(json.write(receipt)).isEqualToJson(expectedReceipt);
    }

    @Test
    void missingRetailerFieldJsonTest() throws IOException {
        ClassPathResource resource = new ClassPathResource(MISSING_RETAILER_FIELD_JSON_PATH);

        ObjectMapper objectMapper = new ObjectMapper();

        Receipt receipt = objectMapper.readValue(resource.getInputStream(), Receipt.class);

        Set<ConstraintViolation<Receipt>> violations = validator.validate(receipt);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Retailer name cannot be blank");
    }

    @Test
    void missingPurchaseDateJsonTest() throws IOException {
        ClassPathResource resource = new ClassPathResource(MISSING_PURCHASE_DATE_FIELD_JSON_PATH);

        ObjectMapper objectMapper = new ObjectMapper();

        Receipt receipt = objectMapper.readValue(resource.getInputStream(), Receipt.class);

        Set<ConstraintViolation<Receipt>> violations = validator.validate(receipt);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Purchase date cannot be blank");
    }

    @Test
    void missingPurchaseTimeJsonTest() throws IOException {
        ClassPathResource resource = new ClassPathResource(MISSING_PURCHASE_TIME_FIELD_JSON_PATH);

        ObjectMapper objectMapper = new ObjectMapper();

        Receipt receipt = objectMapper.readValue(resource.getInputStream(), Receipt.class);

        Set<ConstraintViolation<Receipt>> violations = validator.validate(receipt);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Purchase time cannot be blank");
    }

    @Test
    void missingTotalJsonTest() throws IOException {
        ClassPathResource resource = new ClassPathResource(MISSING_TOTAL_FIELD_JSON_PATH);

        ObjectMapper objectMapper = new ObjectMapper();

        Receipt receipt = objectMapper.readValue(resource.getInputStream(), Receipt.class);

        Set<ConstraintViolation<Receipt>> violations = validator.validate(receipt);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Total amount cannot be blank");

    }

    @Test
    void emptyItemListJsonTest() throws IOException {

        ClassPathResource resource = new ClassPathResource(EMPTY_ITEM_JSON_PATH);

        ObjectMapper objectMapper = new ObjectMapper();

        Receipt receipt = objectMapper.readValue(resource.getInputStream(), Receipt.class);

        Set<ConstraintViolation<Receipt>> violations = validator.validate(receipt);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("At least one item is required");
    }

    @Test
    void invalidRetailerNameJsonTest() throws JsonProcessingException {

        String invalidRetailerJsonString = "{"
                + "\"retailer\": \"Invalid@Name\","
                + "\"purchaseDate\": \"2022-01-02\","
                + "\"purchaseTime\": \"08:13\","
                + "\"total\": \"2.65\","
                + "\"items\": [{"
                + "\"shortDescription\": \"Pepsi - 12-oz\","
                + "\"price\": \"1.25\""
                + "}]"
                + "}";

        ObjectMapper objectMapper = new ObjectMapper();

        Receipt receipt = objectMapper.readValue(invalidRetailerJsonString, Receipt.class);

        Set<ConstraintViolation<Receipt>> violations = validator.validate(receipt);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Retailer name contains invalid characters");
    }

    @Test
    void invalidPurchaseDateJsonTest()  {
        String invalidPurchaseDateJsonString = "{"
                + "\"retailer\": \"Walgreen\","
                + "\"purchaseDate\": \"20-01-2022\","  // Incorrect date format
                + "\"purchaseTime\": \"08:13\","
                + "\"total\": \"2.65\","
                + "\"items\": [{"
                + "\"shortDescription\": \"Pepsi - 12-oz\","
                + "\"price\": \"1.25\""
                + "}]"
                + "}";
        ObjectMapper objectMapper = new ObjectMapper();

        IOException thrown = Assertions.assertThrows(IOException.class, () -> {
            objectMapper.readValue(invalidPurchaseDateJsonString, Receipt.class);
        });

        assertThat(thrown.getMessage()).contains("Invalid date format. Expected format is yyyy-MM-dd.");
    }

    @Test
    void invalidPurchaseTimeJsonTest() {
        String invalidPurchaseDateJsonString = "{"
                + "\"retailer\": \"Walgreen\","
                + "\"purchaseDate\": \"2022-01-20\","
                + "\"purchaseTime\": \"08:13:00\"," // Incorrect purchase time format
                + "\"total\": \"2.65\","
                + "\"items\": [{"
                + "\"shortDescription\": \"Pepsi - 12-oz\","
                + "\"price\": \"1.25\""
                + "}]"
                + "}";

        ObjectMapper objectMapper = new ObjectMapper();

        IOException thrown = Assertions.assertThrows(IOException.class, () -> {
            objectMapper.readValue(invalidPurchaseDateJsonString, Receipt.class);
        });

        assertThat(thrown.getMessage()).contains("Invalid time format. Expected format is HH:mm");
    }

    @Test
    void invalidTotalJsonTest() throws JsonProcessingException  {
        String invalidTotalJsonString = "{"
                + "\"retailer\": \"Walgreen\","
                + "\"purchaseDate\": \"2022-01-20\","
                + "\"purchaseTime\": \"08:13\","
                + "\"total\": \"2.6500\"," // Incorrect total format
                + "\"items\": [{"
                + "\"shortDescription\": \"Pepsi - 12-oz\","
                + "\"price\": \"1.25\""
                + "}]"
                + "}";

        ObjectMapper objectMapper = new ObjectMapper();

        Receipt receipt = objectMapper.readValue(invalidTotalJsonString, Receipt.class);

        Set<ConstraintViolation<Receipt>> violations = validator.validate(receipt);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Receipt total amount must be in the format X.XX");
    }

    @Test
    void invalidItemShortDescriptionJsonTest() throws JsonProcessingException {
        String invalidItemShortDescriptionJsonString = "{"
                + "\"retailer\": \"Walgreen\","
                + "\"purchaseDate\": \"2022-01-20\","
                + "\"purchaseTime\": \"08:13\","
                + "\"total\": \"2.65\","
                + "\"items\": [{"
                + "\"shortDescription\": \"Pepsi # %- 12-oz\"," // Invalid item short description
                + "\"price\": \"1.25\""
                + "}]"
                + "}";
        ObjectMapper objectMapper = new ObjectMapper();

        Receipt receipt = objectMapper.readValue(invalidItemShortDescriptionJsonString, Receipt.class);

        Set<ConstraintViolation<Receipt>> violations = validator.validate(receipt);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Item description contains invalid characters");
    }

    @Test
    void invalidItemPriceJsonTest() throws JsonProcessingException {
        String invalidItemPriceJsonString = "{"
                + "\"retailer\": \"Walgreen\","
                + "\"purchaseDate\": \"2022-01-20\","
                + "\"purchaseTime\": \"08:13\","
                + "\"total\": \"2.65\","
                + "\"items\": [{"
                + "\"shortDescription\": \"Pepsi - 12-oz\","
                + "\"price\": \"1.250\"" // Invalid item price format
                + "}]"
                + "}";

        ObjectMapper objectMapper = new ObjectMapper();

        Receipt receipt = objectMapper.readValue(invalidItemPriceJsonString, Receipt.class);

        Set<ConstraintViolation<Receipt>> violations = validator.validate(receipt);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Item price amount must be in the format X.XX");
    }
}
