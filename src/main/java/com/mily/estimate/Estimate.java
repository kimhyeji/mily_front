package com.mily.estimate;

import com.mily.article.milyx.category.entity.FirstCategory;
import com.mily.article.milyx.category.entity.SecondCategory;
import com.mily.user.MilyUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class Estimate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private FirstCategory firstCategory;

    @ManyToOne
    private SecondCategory secondCategory;

    private String area;

    private String body;

    private LocalDateTime createDate;

    @ManyToOne
    private MilyUser milyUser;
}