package com.fetch.receiptprocessor.service;

import com.fetch.receiptprocessor.model.Receipt;

public interface ReceiptService {
    String processReceipt(Receipt receipt);
    int calculatePoints(String receiptId);
}
