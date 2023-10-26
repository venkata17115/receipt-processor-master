package com.srinivas.receiptprocessor.repository;

import com.srinivas.receiptprocessor.jpa.ItemRepository;
import com.srinivas.receiptprocessor.jpa.ReceiptRepository;
import com.srinivas.receiptprocessor.model.Item;
import com.srinivas.receiptprocessor.model.Receipt;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class RepositoryTests {

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void testReceiptRepository() {

        Receipt receipt = Receipt.builder()
                .retailer("Walmart")
                .purchaseDate("2020-01-01")
                .purchaseTime("10:00")
                .total("100.00")
                .points(10).build();



        Receipt savedReceipt = receiptRepository.save(receipt);

        Assertions.assertThat(savedReceipt).isNotNull();
        Assertions.assertThat(savedReceipt.getId()).isNotNull();

    }

    @Test
    public void testReceiptRespositoryTwoOrMoreReceipts(){
        Receipt receipt = Receipt.builder()
                .retailer("Walmart")
                .purchaseDate("2020-01-01")
                .purchaseTime("10:00")
                .total("100.00")
                .points(10).build();

        Receipt receipt1 = Receipt.builder()
                .retailer("Target")
                .purchaseDate("2021-01-01")
                .purchaseTime("11:00")
                .total("99.00")
                .points(12).build();

        receiptRepository.save(receipt);
        receiptRepository.save(receipt1);

        List<Receipt> receipts = receiptRepository.findAll();

        Assertions.assertThat(receipts).isNotNull();
        Assertions.assertThat(receipts.size()).isEqualTo(2);

    }

    @Test
    public void testItemRepository() {

        Item item = Item.builder()
                .shortDescription("Milk")
                .price("10.00")
                .build();

        Item savedItem = itemRepository.save(item);

        Assertions.assertThat(savedItem).isNotNull();
        Assertions.assertThat(savedItem.getId()).isNotNull();

    }

    @Test
    public void testItemRepositoryTwoOrMoreItems(){
        Item item = Item.builder()
                .shortDescription("Milk")
                .price("10.00")
                .build();

        Item item1 = Item.builder()
                .shortDescription("Bread")
                .price("5.00")
                .build();

        itemRepository.save(item);
        itemRepository.save(item1);

        List<Item> items = itemRepository.findAll();

        Assertions.assertThat(items).isNotNull();
        Assertions.assertThat(items.size()).isEqualTo(2);

    }
}
