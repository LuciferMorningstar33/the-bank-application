package com.prithvi.thebankapplication.utils;

import java.time.Year;
import java.util.Random;

public class AccountUtils {

    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "This user already has an account Created!";
    public static final String ACCOUNT_CREATION_SUCCESS = "002";
    public static final String ACCOUNT_CREATION_MESSAGE = "Account has been successfully created!";
    public static final String ACCOUNT_NOT_EXIST_CODE = "003";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE = "User with provided account number does not exist";
    public static final String ACCOUNT_FOUND_CODE = "004";
    public static final String ACCOUNT_FOUND_SUCCESS = "User Account Found";
    public static final String ACCOUNT_CREDITED_SUCCESS = "005";
    public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE = "Account has been credited successfully with amount :";
    public static final String ACCOUNT_DEPOSITED_SUCCESS = "006";
    public static final String ACCOUNT_DEPOSITED_SUCCESS_MESSAGE = "Account has been deposited successfully with amount :";
    public static final String ACCOUNT_DEPOSITED_FAILURE = "007";
    public static final String ACCOUNT_DEPOSITED_FAILURE_MESSAGE = "Insufficient funds to deposit the amount from your account";
    public static final String TRANSFER_SUCCESS_CODE = "008";
    public static final String TRANSFER_SUCCESS_MESSAGE = "Amount has been transferred successfully to account :";
    public static final String TRANSFER_FAILURE_CODE = "009";
    public static final String TRANSFER_FAILURE_MESSAGE ="Insufficient funds to transfer the amount from your account";

    public static String genearteAccountNumber() {
        /**
         * 2023 + randomSixDigits
         */

        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;

        //generate random number between min and max
        //    int randNumber = new Random().nextInt(min, max+1);
        int randNumber = min + (int) Math.floor(Math.random() * ((max - min) + 1));
        //convert the current and randomNumber to strings, then concatenate

        String year = String.valueOf(currentYear);
        String randomNumber = String.valueOf(randNumber);

        StringBuilder accountNumber = new StringBuilder();
        return accountNumber.append(year).append(randomNumber).toString();
    }
}
