package com.prithvi.thebankapplication.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.prithvi.thebankapplication.dto.EmailDetails;
import com.prithvi.thebankapplication.entity.Transaction;
import com.prithvi.thebankapplication.entity.User;
import com.prithvi.thebankapplication.repository.TransactionRepository;
import com.prithvi.thebankapplication.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class BankStatement implements BankStatementService {

    @Autowired
    private TransactionRepository transactionRepository;
    private UserRepository userRepository;
    private EmailService emailService;

    private static final String FILE = "C:\\Users\\Asus\\OneDrive\\Documents\\MyStatement.pdf";

    /**
     * retrieve list of transactions within a date range given an account number
     * generate a pdf file of transactions
     * send the file via email
     */

    @Override
    public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate) throws FileNotFoundException, DocumentException {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

        List<Transaction> transactionList = transactionRepository.findAll().stream().filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
                .filter(transaction -> transaction.getCreatedAt().isAfter(start)).filter(transaction -> transaction.getCreatedAt().isBefore(end)).toList();

        //designing statement
        User user = userRepository.findByAccountNumber(accountNumber);
        String customerName = user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName();

        Rectangle statementSize = new Rectangle(PageSize.A4);
        Document document = new Document(statementSize);
        log.info("setting size of document");
        OutputStream outputStream = new FileOutputStream(FILE);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        PdfPTable bankInfoTable = new PdfPTable(1);
        PdfPCell bankName = new PdfPCell(new Phrase("The Bank of Republic of People"));
        bankName.setBorder(0);
        bankName.setBackgroundColor(BaseColor.BLUE);
        bankName.setPadding(20f);

        PdfPCell bankAddress = new PdfPCell(new Phrase("RB 21, 65 More St, Republic of People"));
        bankAddress.setBorder(0);

        bankInfoTable.addCell(bankName);
        bankInfoTable.addCell(bankAddress);


        PdfPTable statementInfoTable = new PdfPTable(2);
        PdfPCell customerInfo = new PdfPCell(new Phrase("Start Date: " + startDate));
        customerInfo.setBorder(0);
        PdfPCell statement = new PdfPCell(new Phrase("STATEMENT OF ACCOUNT"));
        statement.setBorder(0);
        PdfPCell stopDate = new PdfPCell(new Phrase("End Date" + endDate));
        stopDate.setBorder(0);
        PdfPCell name = new PdfPCell(new Phrase("Customer Name: " + customerName));
        name.setBorder(0);
        PdfPCell space = new PdfPCell();
        PdfPCell address = new PdfPCell(new Phrase("Customer Address: " + user.getAddress()));
        address.setBorder(0);

        PdfPTable transactionTable = new PdfPTable(4);
        PdfPCell date = new PdfPCell(new Phrase("DATE"));
        date.setBorder(0);
        date.setBackgroundColor(BaseColor.BLUE);
        PdfPCell transactionType = new PdfPCell(new Phrase("TRANSACTION TYPE"));
        transactionType.setBorder(0);
        transactionType.setBackgroundColor(BaseColor.BLUE);
        PdfPCell transactionAmount = new PdfPCell(new Phrase("TRANSCATION AMOUNT"));
        transactionAmount.setBorder(0);
        transactionAmount.setBackgroundColor(BaseColor.BLUE);
        PdfPCell status =  new PdfPCell(new Phrase("STATUS"));
        status.setBorder(0);
        status.setBackgroundColor(BaseColor.BLUE);

        transactionTable.addCell(date);
        transactionTable.addCell(transactionType);
        transactionTable.addCell(transactionAmount);
        transactionTable.addCell(status);

        transactionList.forEach(transaction -> {
            transactionTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
            transactionTable.addCell(new Phrase(transaction.getTransactionType()));
            transactionTable.addCell(new Phrase(transaction.getAmount().toString()));
            transactionTable.addCell(new Phrase(transaction.getStatus().toString()));
        });

        statementInfoTable.addCell(customerInfo);
        statementInfoTable.addCell(statement);
        statementInfoTable.addCell(stopDate);
        statementInfoTable.addCell(name);
        statementInfoTable.addCell(space);
        statementInfoTable.addCell(address);

        document.add(bankInfoTable);
        document.add(statementInfoTable);
        document.add(transactionTable);
        document.close();

        //Email
        EmailDetails emailDetails = EmailDetails.builder()
                .receipent(user.getEmail())
                .subject("STATEMENT OF ACCOUNT")
                .messageBody("Kindly find your requested account statement attached!")
                .attachement(FILE)
                .build();
        emailService.sendEmailWithAttachment(emailDetails);

        return transactionList;
    }
}
