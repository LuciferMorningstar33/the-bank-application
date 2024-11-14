package com.prithvi.thebankapplication.controller;

import com.prithvi.thebankapplication.dto.*;
import com.prithvi.thebankapplication.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Account Management APIs")
public class UserController {

    @Autowired
    UserService userService;

    @Operation(
            summary = "Create a New User Account",
            description = "Creating a new user and assigning an account ID"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Http Status 201 CREATED"
    )
    @PostMapping
    public BankResponse createAccount(@RequestBody UserRequest userRequest) {
        return userService.createAccount(userRequest);
    }

    @Operation(
            summary = "Balance Enquiry",
            description = "Given an account number, check how much the user has"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @GetMapping("balanceEnquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest request) {
        return userService.balanceEnquiry(request);
    }

    @Operation(
            summary = "Name Enquiry",
            description = "Given an account number, check who is the user"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 CREATED"
    )
    @GetMapping("nameEnquiry")
    public String nameEnquiry(@RequestBody EnquiryRequest request) {
        return userService.nameEnquiry(request);
    }

    @Operation(
            summary = "Crediting an account",
            description = "Given an account number and amount, process credit operation"
    )
    @ApiResponse(
            responseCode = "205",
            description = "Http Status 205 CREATED"
    )
    @PostMapping("credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request) {
        return userService.creditAccount(request);
    }

    @Operation(
            summary = "Debiting from an account",
            description = "Given an account number and amount, process debit operation"
    )
    @ApiResponse(
            responseCode = "206",
            description = "Http Status 206 CREATED"
    )
    @PostMapping("debit")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request) {
        return userService.debitAccount(request);
    }

    @Operation(
            summary = "Transferring an amount from an account to another account",
            description = "Given an account number and amount, process debit operation"
    )
    @ApiResponse(
            responseCode = "208",
            description = "Http Status 208 CREATED"
    )
    @PostMapping("transfer")
    public BankResponse transferAccount(@RequestBody TransferRequest request) {
        return userService.transfer(request);
    }

    @GetMapping
    public String fun(){
        return "Live is Server";
    }
}
