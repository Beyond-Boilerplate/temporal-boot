package com.github.sardul3.temporal_boot.common.activities;

import com.github.sardul3.temporal_boot.app.services.TransactionService;
import com.github.sardul3.temporal_boot.common.models.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class SchedulePaymentActivitiesImpl implements SchedulePaymentActivities {

   private final TransactionService transactionService;

   public SchedulePaymentActivitiesImpl(TransactionService transactionService) {
       this.transactionService = transactionService;
   }

    @Override
    public void validateAmount(double amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
    }

    @Override
    public void createScheduledTask(String from, String to, double amount, LocalDateTime scheduledDate) {
        log.info("Creating SchedulePaymentActivities task from {} to {} for amount {} at {}", from, to, amount, scheduledDate);
    }

    @Override
    public Transaction runSchedulePayment(String from, String to, double amount, LocalDateTime scheduledDate) {
        Transaction transaction = new Transaction();
        transaction.setFromAccount(from);
        transaction.setToAccount(to);
        transaction.setAmount(amount);
       return transactionService.saveTransaction(transaction);
    }
}
