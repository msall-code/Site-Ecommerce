package com.maket.maket_backend.modules.workforce.service;

import com.maket.maket_backend.modules.workforce.dto.*;
import com.maket.maket_backend.modules.workforce.model.*;
import com.maket.maket_backend.modules.workforce.repository.*;
import com.maket.maket_backend.modules.blog.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobOfferRepository jobOfferRepository;
    private final JobApplicationRepository jobApplicationRepository;

    public JobResponseDTO createOffer(JobRequestDTO dto, String employerId) {
        JobOffer offer = new JobOffer();
        offer.setTitle(dto.getTitle());
        offer.setDescription(dto.getDescription());
        offer.setSalary(dto.getSalary());
        offer.setCategory(dto.getCategory());
        offer.setEmployerId(employerId);
        return mapToResponseDTO(jobOfferRepository.save(offer));
    }

    public List<JobResponseDTO> getJobsByCategory(Category category) {
        return jobOfferRepository.findByCategory(category).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Transactional
    public void applyToJob(ApplicationRequestDTO dto, String candidateId) {
        JobApplication app = new JobApplication();
        app.setJobOfferId(dto.getJobOfferId());
        app.setCandidateId(candidateId);
        app.setCvUrl(dto.getCvUrl());
        app.setMessage(dto.getMessage());
        jobApplicationRepository.save(app);
    }

    private JobResponseDTO mapToResponseDTO(JobOffer offer) {
        JobResponseDTO dto = new JobResponseDTO();
        dto.setId(offer.getId());
        dto.setTitle(offer.getTitle());
        dto.setDescription(offer.getDescription());
        dto.setSalary(offer.getSalary());
        dto.setCategory(offer.getCategory());
        return dto;
    }
}