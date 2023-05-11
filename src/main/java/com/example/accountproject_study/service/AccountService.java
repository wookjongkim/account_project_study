package com.example.accountproject_study.service;

import com.example.accountproject_study.domain.Account;
import com.example.accountproject_study.domain.AccountUser;
import com.example.accountproject_study.dto.AccountDto;
import com.example.accountproject_study.exception.AccountException;
import com.example.accountproject_study.repository.AccountRepository;
import com.example.accountproject_study.repository.AccountUserRepository;
import com.example.accountproject_study.type.AccountStatus;
import com.example.accountproject_study.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountUserRepository accountUserRepository;
    private final AccountRepository accountRepository;

    /**
     * 사용자가 있는지 조회
     * 계좌 번호 생성하고
     * 계좌를 저장한 후 그 정보를 넘긴다.
     */
    @Transactional
    // 이 메서드드를 실행하는 동안 여러 DB 관련 작업이 하나의 트랜잭션으로 처리됨
    // 즉, 메서드 내의 모든 데이터베이스 작업이 정상적으로 완료되거나, 문제가 발생할 경우 모든 작업이
    // 롤백되어 이전 상태로 되돌림
    public AccountDto createAccount(Long userId, Long initialBalance) {
        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));

        validateAccountUpTen(accountUser);

        String newAccountNumber = accountRepository.findFirstByOrderByIdDesc()
                .map(account -> (Integer.parseInt(account.getAccountNumber())) + 1 + "")
                .orElse("1000000000");

        return AccountDto.fromEntity(
                accountRepository.save(Account.builder()
                        .accountUser(accountUser)
                        .accountStatus(AccountStatus.IN_USE)
                        .accountNumber(newAccountNumber)
                        .balance(initialBalance)
                        .registeredAt(LocalDateTime.now())
                        .build()));
    }

    private void validateAccountUpTen(AccountUser accountUser) {
        if(accountRepository.countByAccountUser(accountUser) >= 10){
            throw new AccountException(ErrorCode.MAX_ACCOUNT_PER_USER_10);
        }
    }

    public AccountDto deleteAccount(Long userId, String accountNumber) {
        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() ->
                        new AccountException(ErrorCode.USER_NOT_FOUND));

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateAccount(accountUser, account);
        account.setAccountStatus(AccountStatus.UNREGISTERED);
        account.setUnRegisteredAt(LocalDateTime.now());
        accountRepository.save(account);

        return AccountDto.fromEntity(account);
    }

    private void validateAccount(AccountUser accountUser, Account account) {
        if(accountUser.getId() != account.getAccountUser().getId()){
            throw new AccountException(ErrorCode.USER_ACCOUNT_UN_MATCH);
        }

        if(account.getAccountStatus() == AccountStatus.UNREGISTERED){
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }

        if(account.getBalance() > 0){
            throw new AccountException(ErrorCode.ACCOUNT_BALANCE_NOT_ZERO);
        }
    }
}
