package com.example.accountproject_study.service;

import com.example.accountproject_study.domain.Account;
import com.example.accountproject_study.domain.AccountUser;
import com.example.accountproject_study.domain.Transaction;
import com.example.accountproject_study.dto.TransactionDto;
import com.example.accountproject_study.exception.AccountException;
import com.example.accountproject_study.repository.AccountRepository;
import com.example.accountproject_study.repository.AccountUserRepository;
import com.example.accountproject_study.repository.TransactionRepository;
import com.example.accountproject_study.type.AccountStatus;
import com.example.accountproject_study.type.ErrorCode;
import com.example.accountproject_study.type.TransactionResultType;
import com.example.accountproject_study.type.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.example.accountproject_study.type.TransactionResultType.F;
import static com.example.accountproject_study.type.TransactionResultType.S;
import static com.example.accountproject_study.type.TransactionType.CANCEL;
import static com.example.accountproject_study.type.TransactionType.USE;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final AccountUserRepository accountUserRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public TransactionDto useBalance(Long userId, String accountNumber,
                                     Long amount){
        AccountUser user = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
        validateUseBalance(user, account, amount);

        account.useBalance(amount);

        Transaction transaction = transactionRepository.save(
                Transaction.builder()
                        .transactionType(USE)
                        .transactionResultType(S)
                        .account(account)
                        .amount(amount)
                        .balanceSnapshot(account.getBalance())
                        .transactionId(UUID.randomUUID().toString().replace("-", ""))
                        .transactedAt(LocalDateTime.now())
                        .build()
        );

        return TransactionDto.fromEntity(transaction);
    }

    private void validateUseBalance(AccountUser user, Account account, Long amount) {
        if(!Objects.equals(user.getId(), account.getAccountUser().getId())){
            throw new AccountException(ErrorCode.USER_ACCOUNT_UN_MATCH);
        }

        if(account.getAccountStatus() != AccountStatus.IN_USE){
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }
        if(account.getBalance() < amount){
            throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
        }
    }

    @Transactional
    public void saveFailedUseTransaction(String accountNumber, Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        saveTransaction(USE,F, amount, account);
    }

    private Transaction saveTransaction(
            TransactionType transactionType,
            TransactionResultType transactionResultType, Long amount, Account account) {
        return transactionRepository.save(
                Transaction.builder()
                        .transactionType(transactionType)
                        .transactionResultType(transactionResultType)
                        .account(account)
                        .amount(amount)
                        .balanceSnapshot(account.getBalance())
                        .transactionId(UUID.randomUUID().toString().replace("-", ""))
                        .transactedAt(LocalDateTime.now())
                        .build()
        );
    }

    @Transactional
    public TransactionDto cancelBalance(String transactionId, String accountNumber,
                                        Long amount)
    {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new AccountException(ErrorCode.TRANSACTION_NOT_FOUND));

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateCancelBalance(transaction, account, amount);

        account.cancelBalance(amount);

        return TransactionDto.fromEntity(
                saveTransaction(CANCEL, S, amount, account)
        );
    }

    private void validateCancelBalance(Transaction transaction, Account account, Long amount) {
        if(!Objects.equals(transaction.getAccount().getId(), account.getId())){
            throw new AccountException(ErrorCode.TRANSACTION_ACCOUNT_UN_MATCH);
        }
        if(!Objects.equals(amount, transaction.getAmount())){
            throw new AccountException(ErrorCode.CANCEL_MUST_FULLY);
        }
        if(transaction.getTransactedAt().isBefore(LocalDateTime.now().minusYears(1))){
            throw new AccountException(ErrorCode.TO_OLD_ORDER_TO_CANCEL);
        }
    }

    @Transactional
    public void saveFailedCancelTransaction(String accountNumber, Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        saveTransaction(CANCEL,F, amount, account);
    }
}
