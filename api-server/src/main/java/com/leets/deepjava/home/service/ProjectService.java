package com.leets.deepjava.home.service;

import com.leets.deepjava.home.domain.Project;
import com.leets.deepjava.home.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public List<Project> findByCategory(String category) {
        return projectRepository.findByCategory(category);
    }

    public Project findById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));
    }

    @Transactional
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    @Transactional
    public Project update(Long id, Project updated) {
        Project project = findById(id);
        project.setTitle(updated.getTitle());
        project.setCategory(updated.getCategory());
        project.setSummary(updated.getSummary());
        project.setContent(updated.getContent());
        project.setGithub(updated.getGithub());
        project.setTags(updated.getTags());
        return project;
    }

    @Transactional
    public void delete(Long id) {
        projectRepository.deleteById(id);
    }
}
