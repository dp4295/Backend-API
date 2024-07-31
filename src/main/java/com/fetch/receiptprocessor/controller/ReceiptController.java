package com.fetch.receiptprocessor.controller;

import com.fetch.receiptprocessor.dto.PointResponse;
import com.fetch.receiptprocessor.dto.ReceiptResponse;
import com.fetch.receiptprocessor.model.Receipt;
import com.fetch.receiptprocessor.service.ReceiptPointServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {

    private final ReceiptPointServiceImpl receiptPointService;

    @Autowired
    public ReceiptController(ReceiptPointServiceImpl receiptPointService) {
        this.receiptPointService = receiptPointService;
    }

//    @PostMapping("/process")
//    public ResponseEntity<?> processReceipt(@Valid @RequestBody Receipt receipt) {
//        try {
//            String receiptId = receiptPointService.processReceipt(receipt);
//            return ResponseEntity.ok().body(new ReceiptResponse(receiptId));
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The receipt is invalid");
//        }
//    }
    @PostMapping("/process")
    public ResponseEntity<?> processReceipt(@Valid @RequestBody Receipt receipt) {
        String receiptId = receiptPointService.processReceipt(receipt);
        return ResponseEntity.ok().body(new ReceiptResponse(receiptId));
    }

    @GetMapping("/{id}/points")
    public ResponseEntity<?> getPoints(@PathVariable("id") String receiptId) {
        try {
            int points = receiptPointService.calculatePoints(receiptId);
            return ResponseEntity.ok().body(new PointResponse(points));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No receipt found for that id");
        }
    }
}

