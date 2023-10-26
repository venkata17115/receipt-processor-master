package com.srinivas.receiptprocessor.jpa;

import com.srinivas.receiptprocessor.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ReceiptRepository is a repository class for the Receipt
 */
@Repository
public interface  ReceiptRepository extends JpaRepository<Receipt, String> {

}
