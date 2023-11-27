package com.mily.estimate;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEstimate is a Querydsl query type for Estimate
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEstimate extends EntityPathBase<Estimate> {

    private static final long serialVersionUID = 1648097238L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEstimate estimate = new QEstimate("estimate");

    public final StringPath area = createString("area");

    public final StringPath body = createString("body");

    public final DateTimePath<java.time.LocalDateTime> createDate = createDateTime("createDate", java.time.LocalDateTime.class);

    public final com.mily.article.milyx.category.entity.QFirstCategory firstCategory;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.mily.user.QMilyUser milyUser;

    public final com.mily.article.milyx.category.entity.QSecondCategory secondCategory;

    public QEstimate(String variable) {
        this(Estimate.class, forVariable(variable), INITS);
    }

    public QEstimate(Path<? extends Estimate> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEstimate(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEstimate(PathMetadata metadata, PathInits inits) {
        this(Estimate.class, metadata, inits);
    }

    public QEstimate(Class<? extends Estimate> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.firstCategory = inits.isInitialized("firstCategory") ? new com.mily.article.milyx.category.entity.QFirstCategory(forProperty("firstCategory")) : null;
        this.milyUser = inits.isInitialized("milyUser") ? new com.mily.user.QMilyUser(forProperty("milyUser"), inits.get("milyUser")) : null;
        this.secondCategory = inits.isInitialized("secondCategory") ? new com.mily.article.milyx.category.entity.QSecondCategory(forProperty("secondCategory"), inits.get("secondCategory")) : null;
    }

}

