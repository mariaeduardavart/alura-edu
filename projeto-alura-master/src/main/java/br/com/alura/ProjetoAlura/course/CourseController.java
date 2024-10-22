package br.com.alura.ProjetoAlura.course;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.alura.ProjetoAlura.exceptions.CourseNotFoundException;
import br.com.alura.ProjetoAlura.exceptions.ForbiddenOperationException;
import br.com.alura.ProjetoAlura.exceptions.InstructorNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/course")
public class CourseController {

    private final CourseService courseService;
    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/new")
    public ResponseEntity<?> createCourse(@Valid @RequestBody NewCourseDTO newCourse) {
        try {
            Course createdCourse = courseService.createCourse(newCourse);
            logger.info("Course created successfully: {}", createdCourse.getCode());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
        } catch (InstructorNotFoundException ex) {
            logger.warn("Instructor not found: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Instructor not found."));
        } catch (ForbiddenOperationException ex) {
            logger.warn("Operation not allowed: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "Operation not allowed."));
        } catch (Exception ex) {
            logger.error("An unexpected error occurred while creating course: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "An unexpected error occurred: " + ex.getMessage()));
        }
    }

    @PostMapping("/{code}/inactive")
    public ResponseEntity<?> deactivateCourse(@PathVariable("code") String courseCode) {
        try {
            courseService.deactivateCourse(courseCode);
            logger.info("Course {} deactivated successfully.", courseCode);
            return ResponseEntity.ok().build();
        } catch (CourseNotFoundException ex) {
            logger.warn("Course not found for deactivation: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Course not found."));
        } catch (Exception ex) {
            logger.error("An unexpected error occurred while deactivating course: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "An unexpected error occurred. " + ex.getMessage()));
        }
    }
}
