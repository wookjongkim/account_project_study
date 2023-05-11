package com.example.accountproject_study.repository;

import com.example.accountproject_study.domain.Account;
import com.example.accountproject_study.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Integer countByAccountUser(AccountUser accountUser);
    Optional<Account> findFirstByOrderByIdDesc();

    Optional<Account> findByAccountNumber(String accountNumber);
}
