package br.com.alura.ProjetoAlura.course;

import java.util.Collections;

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

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/new")
    public ResponseEntity<?> createCourse(@Valid @RequestBody NewCourseDTO newCourse) {
        try {
            Course createdCourse = courseService.createCourse(newCourse);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
        } catch (InstructorNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Instructor not found."));
        } catch (ForbiddenOperationException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "Operation not allowed."));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "An unexpected error occurred: " + ex.getMessage()));
        }
    }

    @PostMapping("/{code}/inactive")
    public ResponseEntity<?> deactivateCourse(@PathVariable("code") String courseCode) {
        try {
            courseService.deactivateCourse(courseCode);
            return ResponseEntity.ok().build();
        } catch (CourseNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Course not found."));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "An unexpected error occurred. " + ex.getMessage()));
        }
    }
}
