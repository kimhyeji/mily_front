package com.mily.reservation;

import java.time.LocalDateTime;
import com.mily.user.LawyerUser;
import com.mily.user.MilyUser;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Setter
@Component
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
@SuperBuilder
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDateTime reservationTime;

    @ManyToOne
    private MilyUser milyUser;

    @ManyToOne
    private LawyerUser lawyerUser;
}