package com.mysite.sbb.question;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 질문 등록 폼의 데이터를 전달하는 객체 (DTO).
 * 클라이언트의 폼 입력 데이터에 대한 유효성 검증 규칙을 정의.
 */
@Getter // Lombok: 필드에 대한 Getter 메서드 자동 생성.
@Setter // Lombok: 필드에 대한 Setter 메서드 자동 생성.
public class QuestionForm {

    /**
     * 질문 제목 필드.
     * @NotEmpty: 값이 null이거나 빈 문자열(empty string, "")이거나 공백(" ")만으로 이루어질 수 없음을 명시.
     * @Size(max=200): 문자열의 최대 길이가 200자임을 명시.
     */
    @NotEmpty(message="제목은 필수항목입니다.")
    @Size(max=200)
    private String subject;

    /**
     * 질문 내용 필드.
     * @NotEmpty: 값이 null이거나 빈 문자열, 또는 공백만으로 이루어질 수 없음을 명시.
     * (내용 길이에 대한 별도 제약 없음, DB TEXT 타입에 저장 예상.)
     */
    @NotEmpty(message="내용은 필수항목입니다.")
    private String content;
}