package com.lyubo_learning.cams.mapper;

import com.lyubo_learning.cams.dto.JobApplicationRequest;
import com.lyubo_learning.cams.dto.JobApplicationResponse;
import com.lyubo_learning.cams.entity.JobApplication;
import com.lyubo_learning.cams.entity.User;
import org.springframework.stereotype.Component;

@Component
public class JobApplicationMapper {

    public JobApplicationResponse toResponse(JobApplication entity) {
        return JobApplicationResponse.builder()
                .id(entity.getId())
                .companyName(entity.getCompanyName())
                .jobTitle(entity.getJobTitle())
                .status(entity.getStatus())
                .source(entity.getSource())
                .jobUrl(entity.getJobUrl())
                .notes(entity.getNotes())
                .appliedDate(entity.getAppliedDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public JobApplication toEntity(JobApplicationRequest request, User user) {
        JobApplication application = new JobApplication();
        application.setUser(user);
        application.setCompanyName(request.getCompanyName());
        application.setJobTitle(request.getJobTitle());
        application.setStatus(request.getStatus());
        application.setSource(request.getSource());
        application.setJobUrl(request.getJobUrl());
        application.setNotes(request.getNotes());
        application.setAppliedDate(request.getAppliedDate());

        return application;
    }
}
