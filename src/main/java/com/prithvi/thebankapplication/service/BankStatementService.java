package com.prithvi.thebankapplication.service;

import com.itextpdf.text.DocumentException;
import com.prithvi.thebankapplication.entity.Transaction;

import java.io.FileNotFoundException;
import java.util.List;

public interface BankStatementService {
    public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate) throws FileNotFoundException, DocumentException;
}
