package com.mily.schedule;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSchedule is a Querydsl query type for Schedule
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSchedule extends EntityPathBase<Schedule> {

    private static final long serialVersionUID = -805395274L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSchedule schedule = new QSchedule("schedule");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.mily.user.QLawyerUser lawyerUser;

    public final DateTimePath<java.time.LocalDateTime> scheduleEndTime = createDateTime("scheduleEndTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> scheduleStartTime = createDateTime("scheduleStartTime", java.time.LocalDateTime.class);

    public QSchedule(String variable) {
        this(Schedule.class, forVariable(variable), INITS);
    }

    public QSchedule(Path<? extends Schedule> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSchedule(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSchedule(PathMetadata metadata, PathInits inits) {
        this(Schedule.class, metadata, inits);
    }

    public QSchedule(Class<? extends Schedule> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.lawyerUser = inits.isInitialized("lawyerUser") ? new com.mily.user.QLawyerUser(forProperty("lawyerUser"), inits.get("lawyerUser")) : null;
    }

}

