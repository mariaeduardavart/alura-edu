package br.com.alura.ProjetoAlura.registration;

import java.util.ArrayList;
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

    @GetMapping("/registration/report")
    public ResponseEntity<List<RegistrationReportItem>> report() {
        List<RegistrationReportItem> items = new ArrayList<>();

        // TODO: Implementar a Questão 4 - Relatório de Cursos Mais Acessados aqui...
        // Dados fictícios abaixo que devem ser substituídos
        items.add(new RegistrationReportItem(
                "Java para Iniciantes",
                "java",
                "Charles",
                "charles@alura.com.br",
                10L
        ));

        items.add(new RegistrationReportItem(
                "Spring para Iniciantes",
                "spring",
                "Charles",
                "charles@alura.com.br",
                9L
        ));

        items.add(new RegistrationReportItem(
                "Maven para Avançados",
                "maven",
                "Charles",
                "charles@alura.com.br",
                9L
        ));

        return ResponseEntity.ok(items);
    }

}
