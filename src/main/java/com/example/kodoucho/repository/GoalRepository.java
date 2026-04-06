package com.example.kodoucho.repository;

import com.example.kodoucho.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByChildIdOrderByCreatedAtAsc(Long childId);
    Optional<Goal> findByIdAndChildId(Long id, Long childId);
}
