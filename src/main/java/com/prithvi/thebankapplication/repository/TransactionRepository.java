package com.prithvi.thebankapplication.repository;

import com.prithvi.thebankapplication.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
}
