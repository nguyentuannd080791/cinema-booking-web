package com.springboot.cinema.repository;

import com.springboot.cinema.entity.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Integer> {
    Optional<Payment> findByTransactionReference(String transactionReference);
}
