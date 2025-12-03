package com.mysite.sbb.answer;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Setter
@Getter
public class Answer {

    // PK + AUTO_INCREMENT
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;

    private LocalDateTime createDate;

    private LocalDateTime ModifyDate;

    @ManyToOne
    private Question question;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    private SiteUser author;

    // Set 자료형으로 작성한 이유는 voter 속성값이 서로 중복되지 않도록 하기 위해서이다
    @ManyToMany
    Set<SiteUser> voter;
}
