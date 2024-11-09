package com.prithvi.thebankapplication.service;

import com.prithvi.thebankapplication.dto.TransactionDto;

public interface TransactionService {
    void saveTransaction(TransactionDto transaction);
}
