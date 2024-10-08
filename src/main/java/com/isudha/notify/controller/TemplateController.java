package com.isudha.notify.controller;

import com.isudha.notify.dto.CreateTemplateDto;
import com.isudha.notify.dto.GenericSuccessResponseDto;
import com.isudha.notify.model.Template;
import com.isudha.notify.model.TemplateStatus;
import com.isudha.notify.service.TemplateService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/template")
@AllArgsConstructor
public class TemplateController {
    private TemplateService templateService;

    // get template
    @GetMapping("/{id}")
    public ResponseEntity<GenericSuccessResponseDto<Template>> get(@PathVariable("id") UUID templateId) {
        Template template = templateService.get(templateId);
        return new ResponseEntity<>(new GenericSuccessResponseDto<>(true, template), HttpStatus.FOUND);
    }

    // create template
    @PostMapping
    public ResponseEntity<GenericSuccessResponseDto<Template>> create(@RequestBody CreateTemplateDto requestDto) {
        Template newTemplate = templateService.create(requestDto);
        return new ResponseEntity<>(new GenericSuccessResponseDto<>(true, newTemplate), HttpStatus.CREATED);
    }

    // modify template
    @PutMapping("/{id}")
    public ResponseEntity<GenericSuccessResponseDto<Template>> update(@PathVariable("id") UUID templateId, @RequestBody CreateTemplateDto requestDto) {
        Template updatedTemplate = templateService.update(templateId, requestDto);
        return new ResponseEntity<>(new GenericSuccessResponseDto<>(true, updatedTemplate), HttpStatus.OK);
    }

    // change status
    @PutMapping("/{id}/changestatus")
    public ResponseEntity<GenericSuccessResponseDto<Template>> changeStatus(@PathVariable("id") UUID templateId, @RequestBody String status) {
        Template updatedTemplate = templateService.changeStatus(templateId, TemplateStatus.valueOf(status));
        return new ResponseEntity<>(new GenericSuccessResponseDto<>(true, updatedTemplate), HttpStatus.OK);
    }

    // delete template
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTemplate(@PathVariable("id") UUID templateId) {
        boolean deleted = templateService.delete(templateId);

        if(deleted)
            return new ResponseEntity<>("Template deleted successfully.", HttpStatus.OK);
        else
            return new ResponseEntity<>("Template not deleted as emails exists", HttpStatus.FORBIDDEN);

    }
}
