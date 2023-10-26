package com.srinivas.receiptprocessor.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PostResponseDTO is a DTO class for the response of the post
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponseDTO {
    String id;
}
