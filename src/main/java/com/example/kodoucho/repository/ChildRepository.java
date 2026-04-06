package com.example.kodoucho.repository;

import com.example.kodoucho.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ChildRepository extends JpaRepository<Child, Long> {
    List<Child> findByParentUserIdOrderByCreatedAtAsc(Long parentUserId);
    Optional<Child> findByIdAndParentUserId(Long id, Long parentUserId);
}
