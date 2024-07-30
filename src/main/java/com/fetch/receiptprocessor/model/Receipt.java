package com.fetch.receiptprocessor.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fetch.receiptprocessor.util.LocalDateDeserializer;
import com.fetch.receiptprocessor.util.LocalDateSerializer;
import com.fetch.receiptprocessor.util.LocalTimeDeserializer;
import com.fetch.receiptprocessor.util.LocalTimeSerializer;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Receipt {

    @NotBlank(message = "Retailer name cannot be blank")
    @Pattern(regexp = "^[\\w\\s\\-&]+$", message = "Retailer name contains invalid characters")
    private String retailer;

    @NotNull(message = "Purchase date cannot be blank")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate purchaseDate;

    @NotNull(message = "Purchase time cannot be blank")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @JsonSerialize(using = LocalTimeSerializer.class)
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    private LocalTime purchaseTime;

    @NotBlank(message = "Total amount cannot be blank")
    @Pattern(regexp = "^\\d+\\.\\d{2}$", message = "Receipt total amount must be in the format X.XX")
    private String total;

    @Size(min = 1, message = "At least one item is required")
    @Valid
    private List<Item> items;
}
