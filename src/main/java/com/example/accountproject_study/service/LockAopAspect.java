package com.example.accountproject_study.service;

import com.example.accountproject_study.aop.AccountLockIdInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LockAopAspect {

    private final LockService lockService;

    // 어떤 경우 이 Aspect 적용할지
    @Around("@annotation(com.example.accountproject_study.aop.AccountLock) && args(request)")
    public Object aroundMethod(
            ProceedingJoinPoint pjp,
            AccountLockIdInterface request
    ) throws Throwable {
        // lock 취득 시도
        lockService.lock(request.getAccountNumber());
        try{
            // before
            return pjp.proceed();
            // after
        }finally {
            // 실패 성공 둘다 lock을 해제하기
            lockService.unlock(request.getAccountNumber());
        }
    }
}
