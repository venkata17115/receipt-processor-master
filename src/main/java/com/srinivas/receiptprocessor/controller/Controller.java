package com.srinivas.receiptprocessor.controller;

import com.srinivas.receiptprocessor.DTO.PointsResponseDTO;
import com.srinivas.receiptprocessor.DTO.PostResponseDTO;
import com.srinivas.receiptprocessor.model.Receipt;
import com.srinivas.receiptprocessor.service.ReceiptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @Author Venkata
 * Controller class for the Receipt Processor application
 *
 */
@RestController
@RequestMapping("/receipts")
public class Controller {
    /**
     * Logger for the Controller class
     */
    Logger logger = LoggerFactory.getLogger(Controller.class);

    /**
     * ReceiptService object
     */
    @Autowired
    ReceiptService receiptService;



    /**
     * Method to save the receipt
     * @param receipt
     * @return
     */
    @PostMapping("/process")
    public ResponseEntity<? extends Object> saveReceipt(@RequestBody Receipt receipt){

        try{
            logger.info("Receipt received: {}", receipt);
            PostResponseDTO postResponseDTO = receiptService.save(receipt);
            return new ResponseEntity<>(postResponseDTO, HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>("The receipt is invalid", HttpStatus.BAD_REQUEST);
        }

    }


    /**
     * Method to get the points for a receipt
     * @param id
     * @return
     */
    @GetMapping("/{id}/points")
    public ResponseEntity<? extends Object> getPoints(@PathVariable String id){

        Optional<Receipt> optionalReceipt = receiptService.findById(id);
        logger.info("Receipt found: {}", optionalReceipt);
        if (optionalReceipt.isPresent()) {
            int points = receiptService.getPoints(id);
            return new ResponseEntity<>(new PointsResponseDTO(points), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Receipt not found", HttpStatus.NOT_FOUND);
        }
    }



}
