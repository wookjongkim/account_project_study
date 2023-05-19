package com.example.accountproject_study.controller;

import com.example.accountproject_study.aop.AccountLock;
import com.example.accountproject_study.dto.CancelBalance;
import com.example.accountproject_study.dto.QueryTransactionResponse;
import com.example.accountproject_study.dto.TransactionDto;
import com.example.accountproject_study.dto.UseBalance;
import com.example.accountproject_study.exception.AccountException;
import com.example.accountproject_study.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transaction/use")
    @AccountLock
    public UseBalance.Response useBalance(
            @Valid @RequestBody UseBalance.Request request
    ) throws InterruptedException {
        TransactionDto transactionDto = transactionService.useBalance(request.getUserId(),
                request.getAccountNumber(), request.getAmount());

        try{
            Thread.sleep(3000L);
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

    @PostMapping("/transaction/cancel")
    @AccountLock
    public CancelBalance.Response cancelBalance(
            @Valid @RequestBody CancelBalance.Request request
    ) throws InterruptedException {
        try{
            Thread.sleep(3000L);
            return CancelBalance.Response.from(
                    transactionService.cancelBalance(request.getTransactionId(),
                            request.getAccountNumber(), request.getAmount())
            );
        }catch(AccountException e){
            log.error("Failed to use balance. ");

            transactionService.saveFailedCancelTransaction(
                    request.getAccountNumber(),
                    request.getAmount()
            );

            throw e;
        }
    }

    @GetMapping("/transaction/{transactionId}")
    public QueryTransactionResponse queryTransaction(
            @PathVariable String transactionId
    ){
        return QueryTransactionResponse.from(
                transactionService.queryTransaction(transactionId)
        );
    }
}
