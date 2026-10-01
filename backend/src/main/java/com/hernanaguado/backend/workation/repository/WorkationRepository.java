package com.hernanaguado.backend.workation.repository;

import com.hernanaguado.backend.workation.model.Workation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkationRepository extends JpaRepository<Workation, String> {
}