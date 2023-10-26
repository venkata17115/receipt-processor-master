package com.srinivas.receiptprocessor.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

/**
 * Receipt is a model class for the Receipt
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "items")
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "The retailer field cannot be blank")
    private String retailer;

    @NotBlank(message = "The purchase date field cannot be blank")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$")
    private String purchaseDate;

    @NotBlank(message = "The purchase time field cannot be blank")
    @Pattern(regexp = "([01]?[0-9]|2[0-3]):[0-5][0-9]", message = "The purchase time must be valid and in the format '00:00' 24 hours format")
    private String purchaseTime;

    @NotBlank(message = "The total field cannot be blank")
    private String total;

    private int points;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL)
    private List<Item> items;

}
