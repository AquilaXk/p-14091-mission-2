package com.mysite.sbb.question;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
     * q: Root 자료형으로, 즉 기준이 되는 Question 엔티티의 객체를 의미하며 질문 제목과 내용을 검색하기 위해 필요하다.
     * u1: Question 엔티티와 SiteUser 엔티티를 아우터 조인(여기서는 JoinType.LEFT로 아우터 조인을 적용한다.)하여 만든 SiteUser 엔티티의 객체이다.
     * Question 엔티티와 SiteUser 엔티티는 author 속성으로 연결되어 있어서 q.join("author")와 같이 조인해야 한다. u1 객체는 질문 작성자를 검색하기 위해 필요하다.
     * a: Question 엔티티와 Answer 엔티티를 아우터 조인하여 만든 Answer 엔티티의 객체이다.
     * Question 엔티티와 Answer 엔티티는 answerList 속성으로 연결되어 있어서 q.join("answerList")와 같이 조인해야 한다. a 객체는 답변 내용을 검색할 때 필요하다.
     * u2: 바로 앞에 작성한 a 객체와 다시 한번 SiteUser 엔티티와 아우터 조인하여 만든 SiteUser 엔티티의 객체로 답변 작성자를 검색할 때 필요하다.
     * */
    private Specification<Question> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(q.get("subject"), "%" + kw + "%"), // 제목
                        cb.like(q.get("content"), "%" + kw + "%"),      // 내용
                        cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자
                        cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용
                        cb.like(u2.get("username"), "%" + kw + "%"));   // 답변 작성자
            }
        };
    }


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
    public void create(String subject, String content, SiteUser user) {
        // 새로운 Question 엔티티 객체 생성.
        Question q = new Question();

        // 제목, 내용 설정.
        q.setSubject(subject);
        q.setContent(content);

        // 생성 일시를 현재 시간으로 설정.
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(user);

        // Repository를 통해 데이터베이스에 엔티티 저장.
        this.questionRepository.save(q);
    }

    public Page<Question> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Specification<Question> spec = search(kw);
        return this.questionRepository.findAllByKeyword(kw, pageable);
    }

    public void modify(Question question, String subject, String content) {
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);
    }

    public void delete(Question question) {
        this.questionRepository.delete(question);
    }

    public void vote(Question question, SiteUser siteUser) {
        question.getVoter().add(siteUser);
        this.questionRepository.save(question);
    }
}