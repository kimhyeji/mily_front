package com.mily.article.milyx.comment;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMilyXComment is a Querydsl query type for MilyXComment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMilyXComment extends EntityPathBase<MilyXComment> {

    private static final long serialVersionUID = 1054826734L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMilyXComment milyXComment = new QMilyXComment("milyXComment");

    public final BooleanPath adopt = createBoolean("adopt");

    public final com.mily.user.QMilyUser author;

    public final StringPath body = createString("body");

    public final DateTimePath<java.time.LocalDateTime> createDate = createDateTime("createDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.mily.article.milyx.QMilyX milyX;

    public final DateTimePath<java.time.LocalDateTime> modifyDate = createDateTime("modifyDate", java.time.LocalDateTime.class);

    public QMilyXComment(String variable) {
        this(MilyXComment.class, forVariable(variable), INITS);
    }

    public QMilyXComment(Path<? extends MilyXComment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMilyXComment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMilyXComment(PathMetadata metadata, PathInits inits) {
        this(MilyXComment.class, metadata, inits);
    }

    public QMilyXComment(Class<? extends MilyXComment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new com.mily.user.QMilyUser(forProperty("author"), inits.get("author")) : null;
        this.milyX = inits.isInitialized("milyX") ? new com.mily.article.milyx.QMilyX(forProperty("milyX"), inits.get("milyX")) : null;
    }

}

