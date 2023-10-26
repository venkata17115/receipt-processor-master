package com.srinivas.receiptprocessor.service;

import com.srinivas.receiptprocessor.DTO.PostResponseDTO;
import com.srinivas.receiptprocessor.jpa.ItemRepository;
import com.srinivas.receiptprocessor.jpa.ReceiptRepository;
import com.srinivas.receiptprocessor.model.Item;
import com.srinivas.receiptprocessor.model.Receipt;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


public class ServiceTests {

    @InjectMocks
    private ReceiptService receiptService;

    @Mock
    private ReceiptRepository receiptRepository;

    @Mock
    private ItemRepository itemRepository;

    Receipt receipt;



    @BeforeEach
    public void init() {
        receiptRepository = Mockito.mock(ReceiptRepository.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        receiptService = new ReceiptService(receiptRepository, itemRepository);
        receipt = createTestReceipt();
    }


    @Test
    public void testSave() throws Exception {

        when(receiptRepository.save(Mockito.any(Receipt.class)))
                .thenReturn(receipt);


        PostResponseDTO postResponseDTO = receiptService.save(receipt);

        Assertions.assertThat(postResponseDTO).isNotNull();
        Assertions.assertThat(postResponseDTO.getId()).isNotNull();
    }

    @Test
    public void testCheckDateTime() throws Exception {

        boolean testCheckDateTimeResponse = receiptService.checkDateTime("2099-01-01", "10:00");
        assertEquals(false, testCheckDateTimeResponse);

        boolean testCheckDateTimeResponse1 = receiptService.checkDateTime("2021-01-01", "10:00");
        assertEquals(true, testCheckDateTimeResponse1);
    }


    @Test
    public void testGetPoints(){
        when(receiptRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.ofNullable(receipt));
        int points = receiptService.getPoints("67439e3e-42d1-4f00-9930-0303fb0358d7");
        assertEquals(10, points);
    }

    @Test
    public void testCalculateRetailerNamePoints(){
        int points = receiptService.calculateRetailerNamePoints("Walmart");
        assertEquals(7, points);
    }


    @Test
    public void testCalculateRoundDollarPoints() {
        int points = receiptService.calculateRoundDollarPoints("100.00");
        assertEquals(50, points);

        points = receiptService.calculateRoundDollarPoints("99.99");
        assertEquals(0, points);
    }

    @Test
    public void testCalculateMultipleOf25Points() {
        int points = receiptService.calculateMultipleOf25Points("12.50");
        assertEquals(25, points);

        points = receiptService.calculateMultipleOf25Points("12.49");
        assertEquals(0, points);
    }

    @Test
    public void testCalculateTwoItemPoints() {
        // Test with different sizes
        int points = receiptService.calculateTwoItemPoints(4);
        assertEquals(10, points);

        points = receiptService.calculateTwoItemPoints(3);
        assertEquals(5, points);
    }

    @Test
    public void testCalculateTrimmedLengthPoints() {

        int points = receiptService.calculateTrimmedLengthPoints(receipt.getItems());
        assertEquals(1, points);

    }

    @Test
    public void testPurchaseTimePoints() {
        // Test with time between 14:00 and 16:00
        int points = receiptService.purchaseTimePoints("15:30");
        assertEquals(10, points);

        // Test with time before 14:00
        points = receiptService.purchaseTimePoints("13:30");
        assertEquals(0, points);
    }

    @Test
    public void testCalculatePoints(){
        int points = receiptService.calculatePoints(receipt);
        assertEquals(94, points);
    }

    @Test
    public void testPurchaseDatePoints() {
        // Test with even day
        int points = receiptService.purchaseDatePoints("2023-10-26");
        assertEquals(0, points);

        // Test with odd day
        points = receiptService.purchaseDatePoints("2023-10-27");
        assertEquals(6, points);
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




}