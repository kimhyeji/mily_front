package com.mily.article.milyx.comment;

import com.mily.article.milyx.MilyX;
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
public class MilyXComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    @Column(columnDefinition = "TEXT")
    private String body;

    @ManyToOne
    private MilyUser author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "milyx_id")
    private MilyX milyX;

    @Column(columnDefinition = "boolean default false")
    private boolean adopt;
}