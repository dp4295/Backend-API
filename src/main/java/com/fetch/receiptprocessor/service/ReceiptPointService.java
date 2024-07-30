package com.fetch.receiptprocessor.service;

import com.fetch.receiptprocessor.model.Receipt;

/* TODO: Rules for implementing ReceiptPointService
 * One point for every alphanumeric character in the retailer name.
 * 50 points if the total is a round dollar amount with no cents.
 * 25 points if the total is a multiple of 0.25.
 * 5 points for every two items on the receipt.
 * If the trimmed length of the item description is a multiple of 3, multiply the price by 0.2 and round up to the nearest integer. The result is the number of points earned.
 * 6 points if the day in the purchase date is odd.
 * 10 points if the time of purchase is after 2:00pm and before 4:00pm.
 */
public interface ReceiptPointService {
    String processReceipt(Receipt receipt);
    int calculatePoints(String receiptId);
}
