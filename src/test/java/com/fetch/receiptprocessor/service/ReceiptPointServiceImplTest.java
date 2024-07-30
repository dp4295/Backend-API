package com.fetch.receiptprocessor.service;

import com.fetch.receiptprocessor.model.Item;
import com.fetch.receiptprocessor.model.Receipt;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReceiptPointServiceImplTest {

    @Test
    void testCalculatePoints() {
        Receipt receipt = Receipt.builder()
                .retailer("Target")
                .purchaseDate(LocalDate.parse("2022-01-01"))
                .purchaseTime(LocalTime.parse("13:01"))
                .total("35.35")
                .items(Arrays.asList(
                        Item.builder().shortDescription("Mountain Dew 12PK").price("6.49").build(),
                        Item.builder().shortDescription("Emils Cheese Pizza").price("12.25").build(),
                        Item.builder().shortDescription("Knorr Creamy Chicken").price("1.26").build(),
                        Item.builder().shortDescription("Doritos Nacho Cheese").price("3.35").build(),
                        Item.builder().shortDescription("Klarbrunn 12-PK 12 FL OZ").price("12.00").build()
                ))
                .build();

        ReceiptPointServiceImpl service = new ReceiptPointServiceImpl();
        String receiptId = service.processReceipt(receipt);
        int points = service.calculatePoints(receiptId);

        /*
          Total Points: 28
          Breakdown:
          6 points - retailer name has 6 characters
          10 points - 4 items (2 pairs @ 5 points each)
          3 Points - "Emils Cheese Pizza" is 18 characters (a multiple of 3) item price of 12.25 * 0.2 = 2.45, rounded up is 3 points
          3 Points - "Klarbrunn 12-PK 12 FL OZ" is 24 characters (a multiple of 3) item price of 12.00 * 0.2 = 2.4, rounded up is 3 points
          6 points - purchase day is odd
          + ---------
           = 28 points
         */
        int expectedPoints = 6 + 10 + 3 + 3 + 6;
        assertEquals(expectedPoints, points);
    }

    @Test
    void testCalculatePoint2() {
        Receipt receipt = Receipt.builder()
                .retailer("Walgreen")
                .purchaseDate(LocalDate.parse("2022-01-02"))
                .purchaseTime(LocalTime.parse("15:00")) // Within 2:00pm to 4:00pm window
                .total("40.00") // Round dollar amount and also a multiple of 0.25
                .items(Arrays.asList(
                        Item.builder().shortDescription("Mountain Dew 12PK").price("6.49").build(),
                        Item.builder().shortDescription("Emils Cheese Pizza").price("12.25").build(),
                        Item.builder().shortDescription("Knorr Creamy Chicken").price("1.26").build(),
                        Item.builder().shortDescription("Doritos Nacho Cheese").price("3.35").build(),
                        Item.builder().shortDescription("Klarbrunn 12-PK 12 FL OZ").price("12.00").build()
                ))
                .build();

        // Process the receipt and calculate points
        ReceiptPointServiceImpl service = new ReceiptPointServiceImpl();
        String receiptId = service.processReceipt(receipt);
        int points = service.calculatePoints(receiptId);

        /*
            Total Points: 109
            Breakdown:
            8 points  - retailer name (Walgreen) has 8 alphanumeric characters
            50 points - total is a round dollar amount
            25 points - total is a multiple of 0.25
            10 points - 3:00pm is between 2:00pm and 4:00pm
            10 points - 5 items (2 pairs @ 5 points each)
            3 points  - "Emils Cheese Pizza" (18 characters, price 12.25 * 0.2 = 2.45, rounded up to 3)
            3 points  - "Klarbrunn 12-PK 12 FL OZ" (24 characters, price 12.00 * 0.2 = 2.4, rounded up to 3)
            +---------
            = 109 points
         */
        int expectedPoints = 8 + 50 + 25 + 10 + 10 + 3 + 3;
        assertEquals(expectedPoints, points);
    }

    @Test
    void testCalculatePoint3() {
        Receipt receipt = Receipt.builder()
                .retailer("M&M Corner Market")
                .purchaseDate(LocalDate.parse("2022-03-20"))
                .purchaseTime(LocalTime.parse("14:33"))
                .total("9.00")
                .items(Arrays.asList(
                        Item.builder().shortDescription("Gatorade").price("2.25").build(),
                        Item.builder().shortDescription("Gatorade").price("2.25").build(),
                        Item.builder().shortDescription("Gatorade").price("2.25").build(),
                        Item.builder().shortDescription("Gatorade").price("2.25").build()
                ))
                .build();

        // Process the receipt and calculate points
        ReceiptPointServiceImpl service = new ReceiptPointServiceImpl();
        String receiptId = service.processReceipt(receipt);
        int points = service.calculatePoints(receiptId);

        /*
          Total Points: 109
          Breakdown:
          50 points - total is a round dollar amount
          25 points - total is a multiple of 0.25
          14 points - retailer name (M&M Corner Market) has 14 alphanumeric characters note: '&' is not alphanumeric
          10 points - 2:33pm is between 2:00pm and 4:00pm
          10 points - 4 items (2 pairs @ 5 points each)
          +---------
          = 109 points
        */
        int expectedPoints = 50 + 25 + 14 + 10 + 10 ;
        assertEquals(expectedPoints, points);

    }


    @Test
    public void testCalculatePoints_receiptNotFound() {

        ReceiptPointServiceImpl service = new ReceiptPointServiceImpl();

        String invalidReceiptId = "invalid-receipt-id";

        // Verify that an IllegalArgumentException is thrown with the expected message
        assertThrows(IllegalArgumentException.class, () -> {
            service.calculatePoints(invalidReceiptId);
        }, "Receipt not found");
    }

}
