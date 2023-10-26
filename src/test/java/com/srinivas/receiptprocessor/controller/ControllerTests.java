package com.srinivas.receiptprocessor.controller;

import com.srinivas.receiptprocessor.DTO.PointsResponseDTO;
import com.srinivas.receiptprocessor.DTO.PostResponseDTO;
import com.srinivas.receiptprocessor.model.Item;
import com.srinivas.receiptprocessor.model.Receipt;
import com.srinivas.receiptprocessor.service.ReceiptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ControllerTests{

    @InjectMocks
    private Controller controller;

    @Mock
    private ReceiptService receiptService;

    private Receipt validReceipt;
    private Receipt invalidReceipt;

    @BeforeEach
    void setUp() {
        validReceipt = createTestReceipt();
        invalidReceipt = createInvalidTestReceipt();

    }

    @Test
    void testSaveReceipt_Valid() throws Exception {
        PostResponseDTO postResponseDTO = new PostResponseDTO("bf304611-7746-4f46-b682-1395096106ef");
        Mockito.when(receiptService.save(Mockito.any(Receipt.class))).thenReturn(postResponseDTO);

        ResponseEntity<?> response = controller.saveReceipt(validReceipt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(postResponseDTO, response.getBody());
    }

    @Test
    void testSaveReceipt_Invalid() throws Exception {
        Mockito.when(receiptService.save(Mockito.any(Receipt.class))).thenThrow(new Exception("The receipt is invalid"));

        ResponseEntity<?> response = controller.saveReceipt(invalidReceipt);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("The receipt is invalid", response.getBody());
    }

    @Test
    void testGetPoints_ExistingReceipt() {
        int points = 30;
        Mockito.when(receiptService.findById(Mockito.anyString())).thenReturn(Optional.of(validReceipt));
        Mockito.when(receiptService.getPoints(Mockito.anyString())).thenReturn(points);

        ResponseEntity<?> response = controller.getPoints("receiptId");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new PointsResponseDTO(points), response.getBody());
    }

    @Test
    void testGetPoints_NonExistingReceipt() {
        Mockito.when(receiptService.findById(Mockito.anyString())).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.getPoints("nonExistingReceiptId");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Receipt not found", response.getBody());
    }



    private Receipt createTestReceipt() {
        Item item = Item.builder()
                .shortDescription("Milk")
                .price("10.00")
                .build();

        Item item1 = Item.builder()
                .shortDescription("Bread1")
                .price("5.00")
                .build();

        Receipt receipt = Receipt.builder().id("bf304611-7746-4f46-b682-1395096106ef")
                .retailer("Walmart")
                .purchaseDate("2020-01-01")
                .purchaseTime("10:00")
                .items(List.of(item, item1))
                .total("15.00")
                .points(10).build();

        return receipt;
    }

    public Receipt createInvalidTestReceipt(){
        return new Receipt();
    }



}