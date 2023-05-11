package com.example.accountproject_study.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND("사용자가 없습니다."),
    ACCOUNT_NOT_FOUND("계좌가 없습니다."),
    USER_ACCOUNT_UN_MATCH("사용자의 계좌가 아닙니다"),
    ACCOUNT_ALREADY_UNREGISTERED("계좌가 이미 해지상태 입니다"),
    ACCOUNT_BALANCE_NOT_ZERO("해지할려는 계좌의 잔액이 0원 이상입니다"),
    MAX_ACCOUNT_PER_USER_10("사용자 최대 계좌 갯수는 10개입니다.");

    private final String description;
}
