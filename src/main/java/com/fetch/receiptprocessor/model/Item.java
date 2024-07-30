package com.fetch.receiptprocessor.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @NotBlank (message = "Item description cannot be blank")
    @Pattern(regexp = "^[\\w\\s\\-]+$", message = "Item description contains invalid characters")
    private String shortDescription;

    @NotBlank(message = "Item price cannot be blank")
    @Pattern(regexp = "^\\d+\\.\\d{2}$", message = "Item price amount must be in the format X.XX")
    private String price;
}
