package com.ramennsama.springboot.lms.repository;
import java.util.List;
import com.ramennsama.springboot.lms.entity.Course;
import com.ramennsama.springboot.lms.enums.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    // Yêu cầu Hibernate JOIN sẵn Instructor và Category ngay trong 1 câu SQL
    @EntityGraph(attributePaths = {"instructor", "category"})
    Page<Course> findByInstructorId(Long instructorId, Pageable pageable);

    @Query(value = "SELECT c FROM Course c JOIN FETCH c.instructor JOIN FETCH c.category WHERE c.status = :status",
           countQuery = "SELECT count(c) FROM Course c WHERE c.status = :status")
    Page<Course> findByStatus(@Param("status") CourseStatus status, Pageable pageable);

    List<Course> findTop5ByOrderByCreatedAtDesc();
}
