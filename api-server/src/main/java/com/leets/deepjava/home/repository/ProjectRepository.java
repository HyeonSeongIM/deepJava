package com.leets.deepjava.home.repository;

import com.leets.deepjava.home.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByCategory(String category);
}
