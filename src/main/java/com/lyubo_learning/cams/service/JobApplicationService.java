package com.lyubo_learning.cams.service;

import com.lyubo_learning.cams.dto.JobApplicationRequest;
import com.lyubo_learning.cams.dto.JobApplicationResponse;
import com.lyubo_learning.cams.entity.ApplicationSource;
import com.lyubo_learning.cams.entity.ApplicationStatus;
import com.lyubo_learning.cams.entity.JobApplication;
import com.lyubo_learning.cams.entity.User;
import com.lyubo_learning.cams.exception.ResourceNotFoundException;
import com.lyubo_learning.cams.exception.UnauthorizedAccessException;
import com.lyubo_learning.cams.mapper.JobApplicationMapper;
import com.lyubo_learning.cams.repository.JobApplicationRepository;
import com.lyubo_learning.cams.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.engine.jdbc.cursor.spi.RefCursorSupport;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.print.attribute.standard.JobName;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;
    private final JobApplicationMapper mapper;

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private JobApplication getApplicationOwnedByUser(Long applicationId, Long userId) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!application.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("You do not have access to this application");
        }

        return application;
    }

    public JobApplicationResponse create(JobApplicationRequest request) {
        User user = getAuthenticatedUser();
        JobApplication application = mapper.toEntity(request, user);
        JobApplication saved = jobApplicationRepository.save(application);
        return mapper.toResponse(saved);
    }

    public JobApplicationResponse getById(Long id) {
        User user = getAuthenticatedUser();
        JobApplication application = getApplicationOwnedByUser(id, user.getId());
        return mapper.toResponse(application);
    }

    public List<JobApplicationResponse> getAllForUser() {
        User user = getAuthenticatedUser();
        return jobApplicationRepository.findByUserId(user.getId())
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public JobApplicationResponse update(Long id, JobApplicationRequest request) {
        User user = getAuthenticatedUser();
        JobApplication application = getApplicationOwnedByUser(id, user.getId());

        application.setCompanyName(request.getCompanyName());
        application.setJobTitle(request.getJobTitle());
        application.setStatus(request.getStatus());
        application.setSource(request.getSource());
        application.setJobUrl(request.getJobUrl());
        application.setNotes(request.getNotes());
        application.setAppliedDate(request.getAppliedDate());

        JobApplication saved = jobApplicationRepository.save(application);
        return mapper.toResponse(saved);
    }

    public void delete(Long id) {
        User user = getAuthenticatedUser();
        JobApplication application = getApplicationOwnedByUser(id, user.getId());
        jobApplicationRepository.delete(application);
    }

    public List<JobApplicationResponse> getFiltered(ApplicationStatus status, ApplicationSource source) {
        User user = getAuthenticatedUser();
        Long userId = user.getId();

        List<JobApplication> results;

        if (status != null && source != null) results = jobApplicationRepository.findByUserIdAndStatusAndSource(userId, status, source);
        else if (status != null) results = jobApplicationRepository.findByUserIdAndStatus(userId, status);
        else if (source != null) results = jobApplicationRepository.findByUserIdAndSource(userId, source);
        else results = jobApplicationRepository.findByUserId(userId);

        return results.stream()
                .map(mapper::toResponse)
                .toList();

    }

}
