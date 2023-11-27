package com.mily.article.feed.entity;

import com.mily.user.MilyUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
public class Feed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int view;

    private String subject;

    @Column(columnDefinition = "TEXT")
    private String body;

    @ManyToOne
    private MilyUser author;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int vote;

    @Column(columnDefinition = "boolean default false")
    private boolean hasVote;

    public void updateView(int view) {
        this.view = view;
    }

    public void updateLike(int vote) {
        this.vote = vote;
    }

    public void increaseView() {
        view++;
    }

    public void increaseLike() { vote++; }
}