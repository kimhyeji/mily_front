package com.mily.article.feed.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFeed is a Querydsl query type for Feed
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFeed extends EntityPathBase<Feed> {

    private static final long serialVersionUID = 961325321L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFeed feed = new QFeed("feed");

    public final com.mily.user.QMilyUser author;

    public final StringPath body = createString("body");

    public final DateTimePath<java.time.LocalDateTime> createDate = createDateTime("createDate", java.time.LocalDateTime.class);

    public final BooleanPath hasVote = createBoolean("hasVote");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> modifyDate = createDateTime("modifyDate", java.time.LocalDateTime.class);

    public final StringPath subject = createString("subject");

    public final NumberPath<Integer> view = createNumber("view", Integer.class);

    public final NumberPath<Integer> vote = createNumber("vote", Integer.class);

    public QFeed(String variable) {
        this(Feed.class, forVariable(variable), INITS);
    }

    public QFeed(Path<? extends Feed> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFeed(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFeed(PathMetadata metadata, PathInits inits) {
        this(Feed.class, metadata, inits);
    }

    public QFeed(Class<? extends Feed> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new com.mily.user.QMilyUser(forProperty("author"), inits.get("author")) : null;
    }

}

