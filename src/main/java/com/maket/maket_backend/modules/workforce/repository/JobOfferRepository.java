package com.maket.maket_backend.modules.workforce.repository;

import com.maket.maket_backend.modules.workforce.model.JobOffer;
import com.maket.maket_backend.modules.blog.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {
    List<JobOffer> findByCategory(Category category);
}