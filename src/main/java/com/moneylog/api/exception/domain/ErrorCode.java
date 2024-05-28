package com.moneylog.api.exception.domain;

import static org.springframework.http.HttpStatus.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    COMMON_ACCESS_DENIED("권한이 없습니다.", FORBIDDEN),
    COMMON_INVALID_PARAMETER("잘못된 파라미터입니다.", BAD_REQUEST),
    COMMON_SERVER_ERROR("서버에서 에러가 발생하였습니다.", INTERNAL_SERVER_ERROR),

    AUTH_AUTHENTICATION_FAILED("인증에 실패하셨습니다.", UNAUTHORIZED),
    AUTH_AUTHORIZATION_FAILED("권한이 없습니다.", FORBIDDEN),
    AUTH_EMAIL_EMPTY("이메일이 없습니다.", BAD_REQUEST),
    AUTH_EMAIL_INVALID("잘못된 이메일입니다.", BAD_REQUEST),
    AUTH_JWT_CLAIMS_EMPTY("JWT claims 문자열이 비어 있습니다.", UNAUTHORIZED),
    AUTH_JWT_EXPIRED("만료된 토큰입니다.", UNAUTHORIZED),
    AUTH_JWT_INVALID("잘못된 토큰입니다.", UNAUTHORIZED),
    AUTH_JWT_UNPRIVILEGED("권한이 없는 토큰입니다.", FORBIDDEN),
    AUTH_JWT_UNSUPPORTED("지원되지 않는 토큰입니다.", UNAUTHORIZED),
    AUTH_MEMBER_NOT_EXISTS("존재하지 않는 사용자입니다.", UNAUTHORIZED),
    AUTH_PASSWORD_EMPTY("비밀번호가 없습니다.", BAD_REQUEST),

    BUDGET_REQUEST_EMPTY("1건 이상의 예산을 설정해주세요.", BAD_REQUEST),
    BUDGET_BUDGETAMOUNT_EMPTY("예산액이 없습니다.", BAD_REQUEST),
    BUDGET_BUDGETAMOUNT_INVALID("잘못된 예산액입니다.", BAD_REQUEST),
    BUDGET_TOTALAMOUNT_EMPTY("총 예산액이 없습니다.", BAD_REQUEST),
    BUDGET_TOTALAMOUNT_INVALID("잚못된 예산총액입니다. 최소 10000원 이상의 예산총액을 설정해주세요.", BAD_REQUEST),

    CATEGORY_NOT_EXISTS("존재하지 않는 카테고리입니다.", BAD_REQUEST),
    CATEGORY_EMPTY("카테고리가 없습니다.", BAD_REQUEST),

    EXPENSE_NOT_EXISTS("존재하지 않는 지출입니다.", NOT_FOUND),
    EXPENSE_EXPENDEDAT_EMPTY("지출일시가 없습니다.", BAD_REQUEST),
    EXPENSE_EXPENSEAMOUNT_EMPTY("지출액이 없습니다.", BAD_REQUEST),
    EXPENSE_EXPENSEAMOUNT_MINUS("지출액은 0원 이상이어야 합니다.", BAD_REQUEST),
    EXPENSE_ISEXCLUDETOTAL_EMPTY("합계제외여부가 없습니다.", BAD_REQUEST),

    MEMBER_NOT_EXISTS("존재하지 않는 사용자입니다.", NOT_FOUND),
    MEMBER_EMAIL_EMPTY("이메일이 없습니다.", BAD_REQUEST),
    MEMBER_EMAIL_INVALID("잘못된 이메일입니다.", BAD_REQUEST),
    MEMBER_PASSWORD_EMPTY("비밀번호가 없습니다.", BAD_REQUEST),
    MEMBER_ROLE_INVALID("잘못된 사용자 권한입니다.", BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;
}
