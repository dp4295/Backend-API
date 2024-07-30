package com.fetch.receiptprocessor.service;

import com.fetch.receiptprocessor.model.Receipt;
import com.fetch.receiptprocessor.model.Item;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReceiptPointServiceImpl implements ReceiptPointService {

    private final Map<String, Receipt> receiptStore = new HashMap<>();

    @Override
    public String processReceipt(Receipt receipt) {
        String receiptId = UUID.randomUUID().toString();
        receiptStore.put(receiptId, receipt);
        return receiptId;
    }

    @Override
    public int calculatePoints(String receiptId) {
        Receipt receipt = receiptStore.get(receiptId);
        if (receipt == null) {
            throw new IllegalArgumentException("Receipt not found");
        }

        int points = 0;

        // One point for every alphanumeric character in the retailer name
        points += receipt.getRetailer().replaceAll("[^a-zA-Z0-9]", "").length();

        double total = Double.parseDouble(receipt.getTotal());

        // 50 points if the total is a round dollar amount with no cents
        if (total % 1 == 0) {
            points += 50;
        }

        // 25 points if the total is a multiple of 0.25
        if (total % 0.25 == 0) {
            points += 25;
        }

        // 5 points for every two items on the receipt
        points += (receipt.getItems().size() / 2) * 5;

        // Points based on item description length being a multiple of 3
        for (Item item : receipt.getItems()) {
            int length = item.getShortDescription().trim().length();
            if (length % 3 == 0) {
                double price = Double.parseDouble(item.getPrice());
                points += Math.ceil(price * 0.2);
            }
        }

        // 6 points if the day in the purchase date is odd
        if (receipt.getPurchaseDate().getDayOfMonth() % 2 != 0) {
            points += 6;
        }

        // 10 points if the time of purchase is after 2:00pm and before 4:00pm
        LocalTime purchaseTime = receipt.getPurchaseTime();
        if (purchaseTime.isAfter(LocalTime.of(14, 0)) && purchaseTime.isBefore(LocalTime.of(16, 0))) {
            points += 10;
        }

        return points;
    }

}
