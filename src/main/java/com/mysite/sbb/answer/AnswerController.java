package com.mysite.sbb.answer;


import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.atn.SemanticContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

/**
 * 답변(Answer) 관련 HTTP 요청을 처리하는 컨트롤러.
 * URL 경로 '/answer' 아래의 요청을 담당하며, Service 계층으로 비즈니스 로직을 위임.
 */
@RequestMapping("/answer")
@RequiredArgsConstructor // final 필드에 대한 생성자 기반 의존성 주입 명시.
@Controller
public class AnswerController {

    // 질문 엔티티 관련 비즈니스 로직 Service 빈 주입.
    private final QuestionService questionService;
    // 답변 엔티티 관련 비즈니스 로직 Service 빈 주입.
    private final AnswerService answerService;

    private final UserService userService;

    /**
     * 질문에 대한 답변을 생성하는 엔드포인트.
     * HTTP POST 요청 '/answer/create/{id}' 경로 처리.
     * @param model 뷰 렌더링을 위한 데이터 저장 객체.
     * @param id 답변을 등록할 질문의 고유 ID (경로 변수).
     * @param answerForm 클라이언트가 제출한 폼 데이터(답변 내용)를 받는 객체 (@Valid를 통해 유효성 검증 대상).
     * @param bindingResult 유효성 검증 결과 저장 객체.
     * @return 유효성 검증 실패 시, 오류 메시지를 담아 질문 상세 페이지('question_detail')를 반환.
     * 유효성 검증 성공 시, 해당 질문 상세 페이지로 리다이렉트 처리.
     */
    // 현재 로그인한 사용자의 정보를 알려면 스프링 시큐리티가 제공하는 Principal 객체를 사용해야 한다
    // principal.getName()을 호출하면 현재 로그인한 사용자의 사용자명(사용자ID)을 알 수 있다.
    // @PreAuthorize("isAuthenticated()") 애너테이션이 붙은 메서드는 로그인한 경우에만 실행
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createAnswer(Model model, @PathVariable("id") Integer id, @Valid AnswerForm answerForm, BindingResult bindingResult
    , Principal principal) {
        // ID를 사용하여 답변 대상 Question 엔티티 조회.
        Question question = this.questionService.getQuestion(id);

        SiteUser siteUser = this.userService.getUser(principal.getName());

        // 유효성 검증 결과 확인. 오류 존재 시 폼 오류 처리.
        if (bindingResult.hasErrors()) {
            // 오류가 있을 경우, 질문 데이터를 모델에 추가하고 상세 페이지로 돌아가 오류 메시지를 표시.
            model.addAttribute("question", question);
            return "question_detail";
        }

        // Service 계층에 답변 생성 로직 위임.
        this.answerService.create(question, answerForm.getContent(), siteUser);

        // 답변 생성 성공 후, 해당 질문의 상세 페이지로 리다이렉트하여 이동.
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String answerModify(AnswerForm answerForm, @PathVariable("id") Integer id, Principal principal) {
        Answer answer = this.answerService.getAnswer(id);
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        answerForm.setContent(answer.getContent());
        return "answer_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String answerModify(@Valid AnswerForm answerForm, BindingResult bindingResult,
                               @PathVariable("id") Integer id, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "answer_form";
        }
        Answer answer = this.answerService.getAnswer(id);
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.answerService.modify(answer, answerForm.getContent());
        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
    }
}