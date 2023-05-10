package com.example.accountproject_study.controller;

import com.example.accountproject_study.dto.AccountDto;
import com.example.accountproject_study.dto.CreateAccount;
import com.example.accountproject_study.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @WebMvcTest
 * JPA 기능은 동작하지 않음
 * 여러 스프링 테스트 어노테이션 중, Web(Spring MVC)에만 집중할 수 있는 어노테이션
 * @Controller, @ControllerAdvice 사용 가능
 * 단, @Service @Repository등은 사용 불가
 */
@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    /**
     * 웹 API 테스트할때 사용
     * 스프링 MVC 테스트의 시작점
     * HTTP GET, POST 등에 대해 API 테스트 가능
     */
    MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    /**
     * Service의 createAccount 메서드에 대한 단위 테스트
     * given을 통해 메서드의 동작을 가정하고 then을 통해 실제 결과가 예상과 일치하는지 확인
     */
    void successCreateAccount() throws Exception{
        //given ( Mockito에서 제공하는 메서드)
        // anyLong() : 메서드 호출 시 어떤 Long값이든 매개변수로 전달 될수 있음을 의미
        // -> 이를 통해 구체적인 값을 지정하지 않아도 테스트 대상 메소드가 호출되는 것을 가정
        // WillReturn : Mockito에서 제공하는 메서드로, 가정된 메서드가 특정 값을 반환할 것임을 지정
        given(accountService.createAccount(anyLong(), anyLong()))
                .willReturn(AccountDto.builder()
                        .userId(1L)
                        .accountNumber("1234567890")
                        .registeredAt(LocalDateTime.now())
                        .unRegisteredAt(LocalDateTime.now())
                        .build());

        //then
        mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        objectMapper.writeValueAsString(
                                new CreateAccount.Request(333L, 10000L)
                        )
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andDo(print());
    }
}