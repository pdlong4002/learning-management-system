package com.ramennsama.springboot.lms.repository;

import com.ramennsama.springboot.lms.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    Optional<Lesson> findByIdAndSectionId(Long id, Long sectionId);
}
