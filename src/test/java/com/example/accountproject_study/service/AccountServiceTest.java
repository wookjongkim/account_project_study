package com.example.accountproject_study.service;

import com.example.accountproject_study.domain.Account;
import com.example.accountproject_study.domain.AccountUser;
import com.example.accountproject_study.dto.AccountDto;
import com.example.accountproject_study.exception.AccountException;
import com.example.accountproject_study.repository.AccountRepository;
import com.example.accountproject_study.repository.AccountUserRepository;
import com.example.accountproject_study.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    @DisplayName("createAccount 메서드 테스트")
    void createAccountSuccess(){
        AccountUser user = AccountUser.builder()
                        .id(12L).name("Pobi").build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.of(
                        Account.builder()
                                .accountNumber("1000000012")
                                .build()));

        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .accountUser(user)
                        .accountNumber("1000000015")
                        .build()
                );

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        AccountDto accountDto = accountService.createAccount(1L, 1000L);

        // verify는 특정 메서드가 몇번 호출되었는지 검증할 수 있음
        // argumentCaptor는 Mockito에서 제공하는 클래스로, 특정 메서드에 전달된 인자를 캡쳐할 수 있습니다.
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(12L, accountDto.getUserId());
        assertEquals("1000000013", captor.getValue().getAccountNumber());

        // verify : save() 메서드가 한번 호출되었는지 확인하는 것
        // createAccount 메소드가 save() 메소드를 정확히 한번 호출 했다는걸 검증
        // ArgumentCaptor를 사용하여 save() 메서드에 전달된 Account 객체 캡쳐
        // captor.getValue()는 Account객체를 가져와 getAccountNumber() 메서드의 결과가
        // 예상한 값인 1000000013과 일치하는지 확인
    }

    @Test
    void createFirstAccount(){
        AccountUser user = AccountUser.builder().id(15L)
                .name("Pobi").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.empty());
        given(accountRepository.save(any()))
                .willReturn(Account.builder().accountUser(user)
                        .accountNumber("10000000015")
                        .build()
                );

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        AccountDto accountDto = accountService.createAccount(1L,1000L);

        //then
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(15L, accountDto.getUserId());
        assertEquals("1000000000", captor.getValue().getAccountNumber());
    }

    @Test
    @DisplayName("해당 유저 없음 - 계좌 생성 실패")
    void createAccount_UserNotFound(){
        AccountUser user = AccountUser.builder().id(15L)
                .name("Pobi").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        AccountException exception = assertThrows(
                AccountException.class,
                () -> accountService.createAccount(1L, 1000L)
        );
        assertEquals(exception.getErrorCode(), ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("유저당 최대 계좌는 10개")
    void createAccount_maxAccountIs10(){
        AccountUser user = AccountUser.builder().id(15L).name("Pobi").build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.countByAccountUser(any()))
                .willReturn(10);

        AccountException exception = assertThrows(
                AccountException.class,
                () -> accountService.createAccount(15L, 1000L)
        );
        assertEquals(exception.getErrorCode(), ErrorCode.MAX_ACCOUNT_PER_USER_10);
    }
}