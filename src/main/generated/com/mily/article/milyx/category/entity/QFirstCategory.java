package com.mily.article.milyx.category.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFirstCategory is a Querydsl query type for FirstCategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFirstCategory extends EntityPathBase<FirstCategory> {

    private static final long serialVersionUID = 1433879844L;

    public static final QFirstCategory firstCategory = new QFirstCategory("firstCategory");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final ListPath<SecondCategory, QSecondCategory> secondCategories = this.<SecondCategory, QSecondCategory>createList("secondCategories", SecondCategory.class, QSecondCategory.class, PathInits.DIRECT2);

    public final StringPath title = createString("title");

    public QFirstCategory(String variable) {
        super(FirstCategory.class, forVariable(variable));
    }

    public QFirstCategory(Path<? extends FirstCategory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFirstCategory(PathMetadata metadata) {
        super(FirstCategory.class, metadata);
    }

}

