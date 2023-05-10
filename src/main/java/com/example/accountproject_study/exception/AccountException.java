package com.example.accountproject_study.exception;

import com.example.accountproject_study.type.ErrorCode;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountException extends RuntimeException{
    // 그냥 Exception을 만들면 checkedException
    // AccountUserException, AccountNotFound 등 이렇게 여러개 만들면 좀 번거러움
    private ErrorCode errorCode;
    private String errorMessage;

    public AccountException(ErrorCode errorCode){
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
