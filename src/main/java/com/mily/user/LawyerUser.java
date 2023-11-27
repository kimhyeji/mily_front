package com.mily.user;

import com.mily.schedule.Schedule;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Setter
@Component
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
@SuperBuilder
public class LawyerUser {
    @Id
    private long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "milyUserId")
    public MilyUser milyUser;

    @NotBlank
    private String major;

    @NotBlank
    private String introduce;

    @NotBlank
    private String officeAddress;

    @NotBlank
    private String licenseNumber;

    @NotBlank
    private String area;

    private String profileImgFilePath;

    @OneToMany(mappedBy = "lawyerUser")
    private List<Schedule> schedules;
}