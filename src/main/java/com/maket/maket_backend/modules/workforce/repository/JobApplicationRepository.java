package com.maket.maket_backend.modules.workforce.repository;

import com.maket.maket_backend.modules.workforce.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByJobOfferId(Long jobOfferId);
}