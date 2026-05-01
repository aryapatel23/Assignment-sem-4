package com.example.assignment.question;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

/**
 * Student Service - Business Logic for Student Management (Q2 & Q3)
 * Handles student registration and database operations
 */
@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    /**
     * Register a new student
     * 
     * @param student the student to register
     * @return the registered student with generated ID
     * @throws IllegalArgumentException if student data is invalid
     */
    @Transactional
    public Student registerStudent(Student student) {
        // Validate input
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty");
        }
        if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Student email cannot be empty");
        }
        if (student.getCourse() == null || student.getCourse().trim().isEmpty()) {
            throw new IllegalArgumentException("Student course cannot be empty");
        }

        // Check if email already exists
        if (studentRepository.findByEmail(student.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        return studentRepository.save(student);
    }

    /**
     * Get student by ID
     * 
     * @param id the student ID
     * @return the student if found
     */
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + id));
    }

    /**
     * Get student by email
     * 
     * @param email the student email
     * @return the student if found
     */
    public Student getStudentByEmail(String email) {
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with email: " + email));
    }

    /**
     * Get all students for the dashboard preview
     *
     * @return all saved students
     */
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    /**
     * Update student information
     * 
     * @param id the student ID
     * @param updatedStudent the updated student data
     * @return the updated student
     */
    @Transactional
    public Student updateStudent(Long id, Student updatedStudent) {
        Student student = getStudentById(id);
        
        if (updatedStudent.getName() != null && !updatedStudent.getName().trim().isEmpty()) {
            student.setName(updatedStudent.getName());
        }
        if (updatedStudent.getCourse() != null && !updatedStudent.getCourse().trim().isEmpty()) {
            student.setCourse(updatedStudent.getCourse());
        }
        if (updatedStudent.getPhone() != null && !updatedStudent.getPhone().trim().isEmpty()) {
            student.setPhone(updatedStudent.getPhone());
        }
        
        return studentRepository.save(student);
    }

    /**
     * Delete student
     * 
     * @param id the student ID
     */
    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new IllegalArgumentException("Student not found with ID: " + id);
        }
        studentRepository.deleteById(id);
    }
}
