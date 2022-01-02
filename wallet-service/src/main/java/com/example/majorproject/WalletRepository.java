package com.example.majorproject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {

    @Transactional
    @Modifying
    @Query("update Wallet w set w.balance = w.balance + :amount where w.email= :email")
    void updateWallet(String email, Double amount);

    Wallet findByEmail(String email);
}
