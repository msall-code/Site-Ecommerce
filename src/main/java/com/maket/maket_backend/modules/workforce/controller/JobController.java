package com.maket.maket_backend.modules.workforce.controller;

import com.maket.maket_backend.modules.workforce.dto.*;
import com.maket.maket_backend.modules.workforce.service.JobService;
import com.maket.maket_backend.modules.blog.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/workforce")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping("/offers")
    public ResponseEntity<JobResponseDTO> createOffer(@RequestBody JobRequestDTO dto, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(jobService.createOffer(dto, jwt.getSubject()));
    }

    @PostMapping("/apply")
    public ResponseEntity<String> apply(@RequestBody ApplicationRequestDTO dto, @AuthenticationPrincipal Jwt jwt) {
        jobService.applyToJob(dto, jwt.getSubject());
        return ResponseEntity.ok("Candidature envoyée !");
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<JobResponseDTO>> getByCat(@PathVariable Category category) {
        return ResponseEntity.ok(jobService.getJobsByCategory(category));
    }
}