package com.prithvi.thebankapplication.controller;


import com.itextpdf.text.DocumentException;
import com.prithvi.thebankapplication.entity.Transaction;
import com.prithvi.thebankapplication.service.BankStatement;
import com.prithvi.thebankapplication.service.BankStatementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/bankStatement")
public class TransactionController {

    @Autowired
    BankStatementService bankStatementService;

    @Operation(
            summary = "Generating a Bank Statement and sending via email as pdf",
            description = "Given an account number, start date and end date it will generate history of transaction associated with this account. Please enter in YYYY-MM-DD format"

    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 CREATED"
    )
    @GetMapping
    public List<Transaction> generateBankStatemenet(@RequestParam String accountNumber,
                                                    @RequestParam String startDate,
                                                    @RequestParam String endDate) throws DocumentException, FileNotFoundException {
        return bankStatementService.generateStatement(accountNumber, startDate, endDate);
    }
}
