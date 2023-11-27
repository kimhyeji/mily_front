package com.mily.article.milyx.category.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSecondCategory is a Querydsl query type for SecondCategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSecondCategory extends EntityPathBase<SecondCategory> {

    private static final long serialVersionUID = -1278878980L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSecondCategory secondCategory = new QSecondCategory("secondCategory");

    public final QFirstCategory firstCategory;

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath title = createString("title");

    public QSecondCategory(String variable) {
        this(SecondCategory.class, forVariable(variable), INITS);
    }

    public QSecondCategory(Path<? extends SecondCategory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSecondCategory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSecondCategory(PathMetadata metadata, PathInits inits) {
        this(SecondCategory.class, metadata, inits);
    }

    public QSecondCategory(Class<? extends SecondCategory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.firstCategory = inits.isInitialized("firstCategory") ? new QFirstCategory(forProperty("firstCategory")) : null;
    }

}

