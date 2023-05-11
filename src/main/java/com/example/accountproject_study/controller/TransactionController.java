package com.example.accountproject_study.controller;

import com.example.accountproject_study.dto.TransactionDto;
import com.example.accountproject_study.dto.UseBalance;
import com.example.accountproject_study.exception.AccountException;
import com.example.accountproject_study.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transaction/use")
    public UseBalance.Response useBalance(
            @Valid @RequestBody UseBalance.Request request
    ){
        TransactionDto transactionDto = transactionService.useBalance(request.getUserId(),
                request.getAccountNumber(), request.getAmount());

        try{
            return UseBalance.Response.from(transactionDto);
        }catch(AccountException e){
            log.error("Failed to use balance. ");

            transactionService.saveFailedUseTransaction(
                    request.getAccountNumber(),
                    request.getAmount()
            );

            throw e;
        }
    }
}
