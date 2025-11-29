package com.mysite.sbb.question;

import com.mysite.sbb.answer.Answer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 질문 엔티티 (DB 테이블 'question'과 매핑).
 * 데이터베이스의 질문 레코드를 나타내는 영속성 객체.
 */
@Entity
@Setter // Lombok: 필드에 대한 Setter 메서드 자동 생성.
@Getter // Lombok: 필드에 대한 Getter 메서드 자동 생성.
public class Question {

    // PK(Primary Key) 설정 및 자동 증가(Auto-increment) 전략 정의.
    @Id
    @GeneratedValue(strategy = IDENTITY) // MySQL의 AUTO_INCREMENT와 동일한 IDENTITY 전략 사용.
    private int id; // 질문의 고유 번호.

    // 엔티티가 생성된 일시 저장.
    private LocalDateTime createDate;

    // 테이블 컬럼 길이 제약 조건 설정 (최대 200자).
    @Column(length = 200)
    private String subject; // 질문 제목.

    // 데이터 타입 정의 (TEXT 타입으로 설정하여 긴 내용 저장 가능).
    @Column(columnDefinition = "TEXT")
    private String content; // 질문 내용.

    // 일대다(OneToMany) 관계 정의: 하나의 질문은 여러 개의 답변을 가짐.
    @OneToMany(
            mappedBy = "question", // Answer 엔티티의 'question' 필드에 의해 매핑됨.
            cascade = {CascadeType.REMOVE, CascadeType.PERSIST} // Question 삭제 시 연관된 Answer도 삭제(REMOVE), Question 저장 시 Answer도 함께 저장(PERSIST).
    )
    private List<Answer> answerList = new ArrayList<>(); // 이 질문에 달린 답변 목록.

    /**
     * 답변 엔티티를 생성하고 현재 질문 엔티티와 연결하는 편의 메서드.
     * 질문 엔티티 내부에서 Answer 객체의 초기화 및 양방향 관계 설정을 처리.
     * @param content 생성할 답변의 내용.
     * @return 생성 및 연결된 Answer 엔티티 반환.
     */
    public Answer addAnswer(String content) {
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setQuestion(this); // 양방향 관계 설정 (Answer -> Question).
        answer.setCreateDate(LocalDateTime.now());
        answerList.add(answer); // 양방향 관계 설정 (Question -> AnswerList).

        return answer;
    }
}