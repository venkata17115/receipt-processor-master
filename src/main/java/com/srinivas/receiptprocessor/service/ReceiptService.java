package com.srinivas.receiptprocessor.service;

import com.srinivas.receiptprocessor.DTO.PostResponseDTO;
import com.srinivas.receiptprocessor.jpa.ItemRepository;
import com.srinivas.receiptprocessor.jpa.ReceiptRepository;
import com.srinivas.receiptprocessor.model.Item;
import com.srinivas.receiptprocessor.model.Receipt;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.GenericValidator;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * ReceiptService is a service class for the Receipt
 */
@AllArgsConstructor
@Slf4j
@Service
public class ReceiptService {



    /**
     * ReceiptRepository object
     */
    public ReceiptRepository receiptRepository;

    /**
     * ItemRepository object
     */
    public ItemRepository itemRepository;




    /**
     * Method to check data and time
     * @param purchaseDate, purchaseTime
     * @return
     * @throws ParseException
     */


    public boolean checkDateTime(String purchaseDate, String  purchaseTime){
        log.debug("Checking date and time");

        GenericValidator genericValidator = new GenericValidator();

        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();


        LocalDate receiptDate = LocalDate.parse(purchaseDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalTime receiptTime = LocalTime.parse(purchaseTime, DateTimeFormatter.ofPattern("HH:mm"));



        if (receiptDate.isEqual(currentDate)) {
            if (receiptTime.isAfter(currentTime)) {
                log.debug("Checking date and time end");
                return false;
            }
        } else if (receiptDate.isAfter(currentDate)) {
            log.debug("Checking date and time end");
            return false;
        }

        log.debug("Checking date and time end");
        return genericValidator.isDate(purchaseDate, "yyyy-MM-dd", true);
    }


    /**
     * Method to save the receipt
     * @param receipt
     * @return
     * @throws Exception
     */
    public PostResponseDTO save(Receipt receipt) throws Exception {

        try{
            log.debug("Saving receipt: {}", receipt);

            int points = calculatePoints(receipt);

            if(!checkDateTime(receipt.getPurchaseDate(), receipt.getPurchaseTime())){
                throw new Exception("The receipt is invalid");
            }

            Receipt toSave =
                    Receipt.builder()
                            .id(receipt.getId())
                            .items(receipt.getItems())
                            .points(points)
                            .purchaseTime(receipt.getPurchaseTime())
                            .purchaseDate(receipt.getPurchaseDate())
                            .retailer(receipt.getRetailer())
                            .total(receipt.getTotal())
                            .build();
            Receipt savedReceipt = receiptRepository.save(toSave);

            List<Item> items = receipt.getItems().stream()
                    .peek(item -> item.setReceipt(savedReceipt)).toList();

            itemRepository.saveAll(items);

            log.debug("Receipt saved: {}", savedReceipt);
            return new PostResponseDTO( savedReceipt.getId());
        }
        catch (Exception e){
            System.out.println(e);
            log.error("Error saving receipt: {}", e);
            throw e;
        }

    }

    /**
     * Method to calculate points
     * @param id
     * @return
     */
    public int getPoints(String id){
        Receipt receipt = receiptRepository.findById(id).get();
        log.debug("Getting points for receipt: {}", receipt);
        return receipt.getPoints();
    }


    /**
     * Method to calculate points based on Retailer Name
     * @param retailer
     * @return
     */
    public int calculateRetailerNamePoints(String retailer){
        String retailerAlphaNumeric = retailer.replaceAll("[^A-Za-z0-9]", "");
        log.debug("Retailer name: {}", retailerAlphaNumeric);
        return retailerAlphaNumeric.length();
    }

    /**
     * Method to calculate points based on Total Amount Rounded to the nearest dollar
     * @param total
     * @return
     */
    public int calculateRoundDollarPoints(String total){
        DecimalFormat df = new DecimalFormat("0.00");
        log.debug("Total: {}", total);
        double totalDouble = Double.parseDouble(total);
        if(Double.parseDouble(df.format(totalDouble % 1)) == 0){
            return 50;
        }
        return 0;
    }

    /**
     * Method to calculate points based on Total Amount Rounded to the nearest quarter
     * @param total
     * @return
     */
    public int calculateMultipleOf25Points(String total){
        DecimalFormat df = new DecimalFormat("0.00");
        double totalDouble = Double.parseDouble(total);
        log.debug("Total: {}", total);
        if(Double.parseDouble(df.format(totalDouble % 0.25)) == 0){
            return 25;
        }
        return 0;
    }

    /**
     * Method to calculate points based on
     * @param size
     * @return
     */
    public int calculateTwoItemPoints(int size){
        log.debug("Number of items: {}", size);
        return (size/2)*5;
    }

    /**
     * Method to calculate points based on  trimmed length of the short description of each item
     * @param items
     * @return
     */
    public int calculateTrimmedLengthPoints(List<Item> items){
        int points = 0;

        for(Item item: items){
            if(item.getShortDescription().trim().length() % 3 == 0){
                points += Math.ceil(Double.parseDouble(item.getPrice()) * 0.2);

            }
        }

        log.debug("Trimmed length points: {}", points);
        return points;
    }


    /**
     * Method to calculate points based on purchase date
     * @param purchaseDate
     * @return
     */
    public int purchaseDatePoints(String purchaseDate){
        String[] dateParts = purchaseDate.split("-");
        int day = Integer.parseInt(dateParts[2]);
        log.debug("Day: {}", day);
        if(day% 2 != 0){
            return 6;
        }
        return 0;
    }

    /**
     * Method to calculate points based on purchase time
     * @param purchaseTime
     * @return
     */
    public int purchaseTimePoints(String purchaseTime){
        String[] timeParts = purchaseTime.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);

        if((hours>14 || (hours == 14 && minutes > 0))&& hours <16){
            return 10;
        }
        return 0;
    }


    /**
     * Method to calculate total points
     * @param receipt
     * @return
     */
    public int calculatePoints(Receipt receipt){

        int points = 0;
        points+=calculateRetailerNamePoints(receipt.getRetailer());
        points+=calculateRoundDollarPoints(receipt.getTotal());
        points+=calculateMultipleOf25Points(receipt.getTotal());
        points+=calculateTwoItemPoints(receipt.getItems().size());
        points+= calculateTrimmedLengthPoints(receipt.getItems());
        points+= purchaseDatePoints(receipt.getPurchaseDate());
        points+= purchaseTimePoints(receipt.getPurchaseTime());

        log.debug("Total points: {}", points);

        return points;
    }

    /**
     * Method to get receipt by id
     * @param id
     * @return
     */
    public Optional<Receipt> findById(String id) {
        log.debug("Getting receipt by id: {}", id);
        return receiptRepository.findById(id);
    }
}
