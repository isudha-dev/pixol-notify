package com.isudha.notify.repository;

import com.isudha.notify.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailRepo extends JpaRepository<Email, UUID> {

    Optional<List<Email>> findAllByTemplateId(UUID templateId);
}
