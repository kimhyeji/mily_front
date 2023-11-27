package com.mily.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLawyerUser is a Querydsl query type for LawyerUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLawyerUser extends EntityPathBase<LawyerUser> {

    private static final long serialVersionUID = 1009654938L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLawyerUser lawyerUser = new QLawyerUser("lawyerUser");

    public final StringPath area = createString("area");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath introduce = createString("introduce");

    public final StringPath licenseNumber = createString("licenseNumber");

    public final StringPath major = createString("major");

    public final QMilyUser milyUser;

    public final StringPath officeAddress = createString("officeAddress");

    public final StringPath profileImgFilePath = createString("profileImgFilePath");

    public final ListPath<com.mily.schedule.Schedule, com.mily.schedule.QSchedule> schedules = this.<com.mily.schedule.Schedule, com.mily.schedule.QSchedule>createList("schedules", com.mily.schedule.Schedule.class, com.mily.schedule.QSchedule.class, PathInits.DIRECT2);

    public QLawyerUser(String variable) {
        this(LawyerUser.class, forVariable(variable), INITS);
    }

    public QLawyerUser(Path<? extends LawyerUser> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLawyerUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLawyerUser(PathMetadata metadata, PathInits inits) {
        this(LawyerUser.class, metadata, inits);
    }

    public QLawyerUser(Class<? extends LawyerUser> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.milyUser = inits.isInitialized("milyUser") ? new QMilyUser(forProperty("milyUser"), inits.get("milyUser")) : null;
    }

}

