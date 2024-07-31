package com.fetch.receiptprocessor.controller;

import com.fetch.receiptprocessor.exception.GlobalExceptionHandler;
import com.fetch.receiptprocessor.model.Item;
import com.fetch.receiptprocessor.model.Receipt;
import com.fetch.receiptprocessor.service.ReceiptPointServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ReceiptControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReceiptPointServiceImpl receiptPointService;

    @InjectMocks
    private ReceiptController receiptController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(receiptController)
                .setControllerAdvice(new GlobalExceptionHandler()) // Add this line
                .build();
    }

    @Test
    void testProcessReceiptSuccess() throws Exception {
        String receiptId = "test-id";
        Receipt receipt = new Receipt();
        receipt.setRetailer("Target");
        receipt.setPurchaseDate(LocalDate.parse("2022-01-02"));
        receipt.setPurchaseTime(LocalTime.parse("13:13"));
        receipt.setTotal("1.25");
        receipt.setItems(Collections.singletonList(new Item("Pepsi - 12-oz", "1.25")));

        when(receiptPointService.processReceipt(any(Receipt.class))).thenReturn(receiptId);

        mockMvc.perform(post("/receipts/process")
                        .contentType("application/json")
                        .content("{\"retailer\":\"Target\",\"purchaseDate\":\"2022-01-02\",\"purchaseTime\":\"13:13\",\"total\":\"1.25\",\"items\":[{\"shortDescription\":\"Pepsi - 12-oz\",\"price\":\"1.25\"}]}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(receiptId));

        verify(receiptPointService, times(1)).processReceipt(any(Receipt.class));
    }


    @Test
    void testProcessReceiptInvalidReceipt() throws Exception {
        mockMvc.perform(post("/receipts/process")
                        .contentType("application/json")
                        .content("{\"retailer\":\"Ta%t\",\"purchaseDate\":\"2022-01-02\",\"purchaseTime\":\"13:13\",\"total\":\"1.25\",\"items\":[{\"shortDescription\":\"Pepsi - 12-oz\",\"price\":\"1.25\"}]}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The receipt is invalid"));
    }

    @Test
    void testGetPointsSuccess() throws Exception {
        int points = 100;
        when(receiptPointService.calculatePoints(anyString())).thenReturn(points);

        mockMvc.perform(get("/receipts/test-id/points"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points").value(points));

        verify(receiptPointService, times(1)).calculatePoints(anyString());
    }

    @Test
    void testGetPointsNotFound() throws Exception {
        when(receiptPointService.calculatePoints(anyString()))
                .thenThrow(new IllegalArgumentException("No receipt found for that id"));

        mockMvc.perform(get("/receipts/test-id/points"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No receipt found for that id"));

        verify(receiptPointService, times(1)).calculatePoints(anyString());
    }

    @Test
    void testProcessReceiptIllegalArgumentException() throws Exception {
        when(receiptPointService.processReceipt(any(Receipt.class)))
                .thenThrow(new IllegalArgumentException("The receipt is invalid"));

        mockMvc.perform(post("/receipts/process")
                        .contentType("application/json")
                        .content("{\"retailer\":\"Target\",\"purchaseDate\":\"2022-01-02\",\"purchaseTime\":\"13:13\",\"total\":\"1.25\",\"items\":[{\"shortDescription\":\"Pepsi - 12-oz\",\"price\":\"1.25\"}]}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The receipt is invalid"));

        verify(receiptPointService, times(1)).processReceipt(any(Receipt.class));
    }
}
