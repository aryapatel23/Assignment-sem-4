package com.example.assignment.question;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Student entity
 * Provides CRUD operations and custom queries for Student management
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    /**
     * Find a student by email
     * @param email the student's email
     * @return Optional containing the student if found
     */
    Optional<Student> findByEmail(String email);
    
    /**
     * Find all students by course
     * @param course the course name
     * @return List of students in the given course
     */
    Iterable<Student> findByCourse(String course);
}
