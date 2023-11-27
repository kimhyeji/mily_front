package com.mily.article.milyx;

import com.mily.article.milyx.category.entity.FirstCategory;
import com.mily.article.milyx.category.entity.SecondCategory;
import com.mily.article.milyx.comment.MilyXComment;
import com.mily.user.MilyUser;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
public class MilyX {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int view;

    @Column(nullable = false)
    private int milyPoint;

    private String subject;

    @Column(columnDefinition = "TEXT")
    private String body;

    @ManyToOne
    private MilyUser author;

    @OneToMany(mappedBy = "milyX", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MilyXComment> comments;

    @ManyToOne
    private FirstCategory firstCategory;

    @ManyToOne
    private SecondCategory secondCategory;

    @Column(columnDefinition = "boolean default false")
    private boolean hasAdoptedAnswer;

    public void updateView(int view) {
        this.view = view;
    }

    public void increaseiew() {
        view++;
    }

    public int getCommentsCount() {
        return this.comments.size();
    }
}