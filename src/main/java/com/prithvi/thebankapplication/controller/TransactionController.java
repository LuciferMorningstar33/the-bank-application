package com.prithvi.thebankapplication.controller;


import com.itextpdf.text.DocumentException;
import com.prithvi.thebankapplication.entity.Transaction;
import com.prithvi.thebankapplication.service.BankStatement;
import com.prithvi.thebankapplication.service.BankStatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/bankStatement")
public class TransactionController {

    @Autowired
    BankStatementService bankStatementService;

    @GetMapping
    public List<Transaction> generateBankStatemenet(@RequestParam String accountNumber,
                                                    @RequestParam String startDate,
                                                    @RequestParam String endDate) throws DocumentException, FileNotFoundException {
        return bankStatementService.generateStatement(accountNumber, startDate, endDate);
    }
}
