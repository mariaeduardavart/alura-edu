package br.com.alura.ProjetoAlura.registration;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.alura.ProjetoAlura.exceptions.CourseInactiveException;
import br.com.alura.ProjetoAlura.exceptions.CourseNotFoundException;
import br.com.alura.ProjetoAlura.exceptions.DuplicateRegistrationException;
import br.com.alura.ProjetoAlura.exceptions.RegistrationReportException;
import br.com.alura.ProjetoAlura.exceptions.UserNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/registration")
public class RegistrationController {

    private final RegistrationService registrationService;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/new")
    public ResponseEntity<?> createRegistration(@Valid @RequestBody NewRegistrationDTO newRegistration) {
        try {
            Registration createdRegistration = registrationService.registerStudent(newRegistration);
            logger.info("Registration created successfully for student: {}", newRegistration.getStudentEmail());

            return ResponseEntity.status(HttpStatus.CREATED).body(createdRegistration);

        } catch (CourseNotFoundException ex) {
            logger.warn("Course not found: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Course not found."));
        } catch (CourseInactiveException ex) {
            logger.warn("Attempt to register for an inactive course: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "The course is not active."));
        } catch (UserNotFoundException ex) {
            logger.warn("User not found: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Student not found."));
        } catch (DuplicateRegistrationException ex) {
            logger.warn("Duplicate registration attempt: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Student is already registered for this course."));
        } catch (Exception ex) {
            logger.error("An unexpected error occurred while creating registration: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "An unexpected error occurred: " + ex.getMessage()));
        }
    }

    @GetMapping("/report")
    public ResponseEntity<List<RegistrationReportItem>> report() {
        try {
            List<RegistrationReportItem> items = registrationService.getMostPopularCourses();
            logger.info("Retrieved report for most popular courses with {} registrations.", items.size());
            return ResponseEntity.ok(items);
        } catch (RegistrationReportException ex) {
            logger.error("Error retrieving report of most popular courses: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());  // Retorna uma lista vazia em caso de erro
        } catch (Exception ex) {
            logger.error("Unexpected error while retrieving report: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());  // Retorna uma lista vazia para qualquer erro inesperado
        }
    }

}
