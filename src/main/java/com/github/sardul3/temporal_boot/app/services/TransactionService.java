package com.github.sardul3.temporal_boot.app.services;




import com.github.sardul3.temporal_boot.app.repos.TransactionRepository;
import com.github.sardul3.temporal_boot.common.models.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



@Service
@Slf4j
public class TransactionService {


    private final TransactionRepository transactionRepository;
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

}


