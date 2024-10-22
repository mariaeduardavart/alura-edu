package br.com.alura.ProjetoAlura.user;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.alura.ProjetoAlura.util.ErrorItemDTO;
import jakarta.validation.Valid;

@RestController
public class UserController {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @PostMapping("/user/newStudent")
    public ResponseEntity<?> newStudent(@RequestBody @Valid NewStudentUserDTO newStudent) {
        if (userRepository.existsByEmail(newStudent.getEmail())) {
            logger.warn("Attempt to register new student with already existing email: {}", newStudent.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("email", "Email já cadastrado no sistema"));
        }

        User user = newStudent.toModel();
        userRepository.save(user);
        logger.info("New student registered successfully: {}", newStudent.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/user/all")
    public List<UserListItemDTO> listAllUsers() {
        logger.info("Retrieving all registered users.");
        return userRepository.findAll().stream().map(UserListItemDTO::new).toList();
    }
}

// Note: This controller does not have a service layer for simplicity and to meet the test requirements. 
// In a real application, it would be advisable to implement a service for better separation of concerns.

