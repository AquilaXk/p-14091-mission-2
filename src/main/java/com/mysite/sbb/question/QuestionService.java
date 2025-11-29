package com.mysite.sbb.question;

import com.mysite.sbb.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 질문 엔티티(Question) 관련 비즈니스 로직을 담당하는 서비스 계층 클래스.
 * 데이터베이스 접근은 QuestionRepository에 위임하고, 핵심 비즈니스 기능을 구현.
 */
@RequiredArgsConstructor // final 필드에 대한 생성자 기반 의존성 주입 명시.
@Service // 해당 클래스가 Spring의 서비스 빈임을 선언.
public class QuestionService {

    // 데이터베이스 접근을 담당하는 Repository 빈 의존성 주입.
    private final QuestionRepository questionRepository;


    /**
     * 특정 ID를 가진 질문을 조회하는 메서드.
     * @param id 조회할 질문의 고유 ID.
     * @return 해당 ID의 Question 엔티티 반환.
     * @throws DataNotFoundException 해당 ID의 질문이 존재하지 않을 경우 예외 발생.
     */
    public Question getQuestion(Integer id) {
        // Repository를 통해 ID로 질문을 조회 (Optional 타입 반환).
        Optional<Question> question = this.questionRepository.findById(id);

        // Optional 객체에 Question 엔티티가 존재하는지 확인.
        if (question.isPresent()) {
            return question.get(); // 엔티티 반환.
        } else {
            // 엔티티가 존재하지 않을 경우 사용자 정의 예외 발생.
            throw new DataNotFoundException("question not found");
        }
    }

    /**
     * 새로운 질문을 생성하고 저장하는 메서드.
     * @param subject 생성할 질문의 제목.
     * @param content 생성할 질문의 내용.
     */
    public void create(String subject, String content) {
        // 새로운 Question 엔티티 객체 생성.
        Question q = new Question();

        // 제목, 내용 설정.
        q.setSubject(subject);
        q.setContent(content);

        // 생성 일시를 현재 시간으로 설정.
        q.setCreateDate(LocalDateTime.now());

        // Repository를 통해 데이터베이스에 엔티티 저장.
        this.questionRepository.save(q);
    }

    public Page<Question> getList(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.questionRepository.findAll(pageable);
    }
}