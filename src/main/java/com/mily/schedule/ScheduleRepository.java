package com.mily.schedule;

import com.mily.user.LawyerUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByLawyerUser(LawyerUser lawyerUser);
}
