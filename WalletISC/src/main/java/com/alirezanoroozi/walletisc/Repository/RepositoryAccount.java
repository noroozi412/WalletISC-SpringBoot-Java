package com.alirezanoroozi.walletisc.Repository;

import com.alirezanoroozi.walletisc.Entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositoryAccount extends JpaRepository<Account,Long> {
    public Optional<Account> findByAccountNumber(String accountNumber);

}
