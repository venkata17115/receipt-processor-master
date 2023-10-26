package com.srinivas.receiptprocessor.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * Item is a model class for the Item
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "The Product Name field cannot be blank")
    @Pattern(regexp = "^[\\w\\s\\-]+$", message = "The Short Product Description for the item must contain only word characters (letters, digits, or underscores), spaces, and hyphens. ")
    private String shortDescription;

    @NotBlank
    @Pattern(regexp = "^\\d+\\.\\d{2}$", message = "The total price payed for this item must be in the format '0.00'")
    private String price;

    @ManyToOne()
    @JoinColumn(name = "receipt_id")
    private Receipt receipt;


}
