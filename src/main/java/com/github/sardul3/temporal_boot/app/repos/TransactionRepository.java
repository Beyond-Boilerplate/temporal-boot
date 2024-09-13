package com.github.sardul3.temporal_boot.app.repos;

import com.github.sardul3.temporal_boot.common.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
