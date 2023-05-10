package com.example.accountproject_study.controller;

import com.example.accountproject_study.dto.AccountDto;
import com.example.accountproject_study.dto.CreateAccount;
import com.example.accountproject_study.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/account")
    public CreateAccount.Response createAccount(
            @RequestBody @Valid CreateAccount.Request request
    ){
        // 계좌 생성 및 저장 후 필요한 정보만을 보내기 위해 dto 사용
        return CreateAccount.Response.from(
                accountService.createAccount(
                        request.getUserId(),
                        request.getInitialBalance())
        );
    }
}
