package com.prithvi.thebankapplication.service;

import com.prithvi.thebankapplication.config.JwtTokenProvider;
import com.prithvi.thebankapplication.dto.*;
import com.prithvi.thebankapplication.entity.Role;
import com.prithvi.thebankapplication.entity.User;
import com.prithvi.thebankapplication.repository.UserRepository;
import com.prithvi.thebankapplication.utils.AccountUtils;
import com.prithvi.thebankapplication.utils.EmailUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Override
     public BankResponse createAccount(UserRequest userRequest){
        /**
         * Creating an account - saving a new user into the db
         * check if user already has an account
         */
        if(userRepository.existsByEmail(userRequest.getEmail())){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();

        }

        User newUser = User.builder()
                 .firstName(userRequest.getFirstName())
                 .lastName(userRequest.getLastName())
                 .otherName(userRequest.getOtherName())
                 .gender(userRequest.getGender())
                 .address(userRequest.getAddress())
                 .stateOfOrigin(userRequest.getStateOfOrigin())
                 .accountNumber(AccountUtils.genearteAccountNumber())
                 .accountBalance(BigDecimal.ZERO)
                 .email(userRequest.getEmail())
                 .password(passwordEncoder.encode(userRequest.getPassword()))
                 .phoneNumber(userRequest.getPhoneNumber())
                 .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                 .status("ACTIVE")
                 .role(Role.valueOf("ROLE_USER"))
                 .build();

        User savedUser = userRepository.save(newUser);
        System.out.println(userRequest);
        System.out.println("Hello");

        //Send Mail Alert
        EmailDetails emailDetails = EmailDetails.builder()
                .receipent(savedUser.getEmail())
                .subject(EmailUtils.ACCOUNT_CREATED_SUBJECT)
                .messageBody(EmailUtils.ACCOUNT_CREATED_MESSAGE+"\nAccount Name:"+savedUser.getFirstName()+" "+savedUser.getLastName()+" "+savedUser.getOtherName()+"\nAccount Number: "+savedUser.getAccountNumber())
                .build();
        emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName()+" "+savedUser.getOtherName())
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .build())
                .build();
    }


    public BankResponse login(LoginDto loginDto){
        Authentication authentication = null;
        authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()
        ));

        EmailDetails loginAlert = EmailDetails.builder()
                .subject("You're logged in!")
                .receipent(loginDto.getEmail())
                .messageBody("You have logged into your account. If you did not initiate this request, please conntact our bank immediately")
                .build();

        emailService.sendEmailAlert(loginAlert);

        return BankResponse.builder()
                .responseCode("Login Success")
                .responseMessage(jwtTokenProvider.generateToken(authentication))
                .build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        //check if the provided account number exists in the db
        boolean isAccountExisit = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExisit){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
                .accountInfo(AccountInfo.builder()
                        .accountName(foundUser.getFirstName()+" "+foundUser.getLastName()+" "+foundUser.getOtherName())
                        .accountNumber(foundUser.getAccountNumber())
                        .accountBalance(foundUser.getAccountBalance())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        boolean isAccountExisit = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExisit){
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName() ;
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        boolean isAccountExisit = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExisit){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);

        //Save Transaction
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .amount(request.getAmount())
                .transactionType("CREDIT")
                .build();

        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE + " " + request.getAmount())
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName()+" "+userToCredit.getLastName()+" "+userToCredit.getOtherName())
                        .accountNumber(userToCredit.getAccountNumber())
                        .accountBalance(userToCredit.getAccountBalance())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        boolean isAccountExisit = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExisit){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }


        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
        if(userToDebit.getAccountBalance().compareTo(request.getAmount()) == -1){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_FAILURE)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_FAILURE_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(userToDebit.getAccountNumber())
                            .accountName(userToDebit.getFirstName()+" "+userToDebit.getLastName()+" "+userToDebit.getOtherName())
                            .accountBalance(userToDebit.getAccountBalance())
                            .build())
                    .build();
        }
        userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
        userRepository.save(userToDebit);


        //Save Transaction
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(userToDebit.getAccountNumber())
                .amount(request.getAmount())
                .transactionType("DEBIT")
                .build();

        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToDebit.getFirstName()+" "+userToDebit.getLastName()+" "+userToDebit.getOtherName())
                        .accountNumber(userToDebit.getAccountNumber())
                        .accountBalance(userToDebit.getAccountBalance())
                        .build())
                .build();
    }

    @Override
    public BankResponse transfer(TransferRequest request) {
//        User sourceAccount = userRepository.findByAccountNumber(request.getSourceAccountNumber());
//        CreditDebitRequest debitRequest = CreditDebitRequest.builder()
//                .accountNumber(request.getSourceAccountNumber())
//                .amount(request.getAmount())
//                .build();
//
//        if(sourceAccount.getAccountBalance().compareTo(request.getAmount()) == -1){
//            return BankResponse.builder()
//                    .responseCode(AccountUtils.TRANSFER_FAILURE_CODE)
//                    .responseMessage(AccountUtils.TRANSFER_FAILURE_MESSAGE)
//                    .accountInfo(AccountInfo.builder()
//                            .accountName(sourceAccount.getFirstName()+" "+sourceAccount.getLastName()+" "+sourceAccount.getOtherName())
//                            .accountNumber(sourceAccount.getAccountNumber())
//                            .accountBalance(sourceAccount.getAccountBalance())
//                            .build())
//                    .build();
//
//        }
//
//        debitAccount(debitRequest);
//
//        User dstAccount = userRepository.findByAccountNumber(request.getDestinationAccountNumber());
//        CreditDebitRequest creditRequest = CreditDebitRequest.builder()
//                .accountNumber(request.getDestinationAccountNumber())
//                .amount(request.getAmount())
//                .build();
//
//        creditAccount(creditRequest);
//
//        return BankResponse.builder()
//                .responseCode(AccountUtils.TRANSFER_SUCCESS_CODE)
//                .responseMessage(AccountUtils.TRANSFER_SUCCESS_MESSAGE + " " + request.getDestinationAccountNumber())
//                .accountInfo(AccountInfo.builder()
//                        .accountName(sourceAccount.getFirstName()+" "+sourceAccount.getLastName()+" "+sourceAccount.getOtherName())
//                        .accountNumber(sourceAccount.getAccountNumber())
//                        .accountBalance(sourceAccount.getAccountBalance())
//                        .build())
//                .build();
        boolean isDestinationAccountExists = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
        if(!isDestinationAccountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User sourceAccountUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());
        String sourceAccountName = sourceAccountUser.getFirstName() + " " + sourceAccountUser.getLastName() + " " + sourceAccountUser.getOtherName();
        if(request.getAmount().compareTo(sourceAccountUser.getAccountBalance()) > 0){
            return BankResponse.builder()
                    .responseCode(AccountUtils.TRANSFER_FAILURE_CODE)
                    .responseMessage(AccountUtils.TRANSFER_FAILURE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmount()));
        userRepository.save(sourceAccountUser);

        //Email Alert
        EmailDetails debitAlert = EmailDetails.builder()
                .receipent(sourceAccountUser.getEmail())
                .subject("Debit Alert!")
                .messageBody("The sum of " + request.getAmount() + " has been deducted from your account! Your current account balance is " + sourceAccountUser.getAccountBalance())
                .build();

        emailService.sendEmailAlert(debitAlert);

        //Save Transaction
        TransactionDto transactionDtoDebit = TransactionDto.builder()
                .accountNumber(sourceAccountUser.getAccountNumber())
                .amount(request.getAmount())
                .transactionType("TRANSFER DEBIT")
                .build();

        transactionService.saveTransaction(transactionDtoDebit);

        User destinationAccountUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber());
//        String destinationAccountName = destinationAccountUser.getFirstName() + " " + destinationAccountUser.getLastName() + " " + destinationAccountUser.getOtherName();
        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(request.getAmount()));
        userRepository.save(destinationAccountUser);

        //Email Alert
        EmailDetails creditAlert = EmailDetails.builder()
                .receipent(destinationAccountUser.getEmail())
                .subject("Credit Alert!")
                .messageBody("The sum of " + request.getAmount() + " has been sent to your account by " +  sourceAccountName + "! Your current account balance is " + destinationAccountUser.getAccountBalance())
                .build();

        emailService.sendEmailAlert(creditAlert);

        //Save Transaction
        TransactionDto transactionDtoCredit = TransactionDto.builder()
                .accountNumber(destinationAccountUser.getAccountNumber())
                .amount(request.getAmount())
                .transactionType("TRANSFER CREDIT")
                .build();

        transactionService.saveTransaction(transactionDtoCredit);

        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESS_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(sourceAccountUser.getFirstName()+" "+sourceAccountUser.getLastName()+" "+sourceAccountUser.getOtherName())
                        .accountNumber(sourceAccountUser.getAccountNumber())
                        .accountBalance(sourceAccountUser.getAccountBalance())
                        .build())
                .build();
    }
}
