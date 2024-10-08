package com.isudha.notify.repository;

import com.isudha.notify.model.Template;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TemplateRepo extends JpaRepository<Template, UUID> {
}
