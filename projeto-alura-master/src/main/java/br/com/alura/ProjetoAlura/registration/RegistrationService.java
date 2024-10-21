package br.com.alura.ProjetoAlura.registration;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.alura.ProjetoAlura.course.Course;
import br.com.alura.ProjetoAlura.course.CourseRepository;
import br.com.alura.ProjetoAlura.exceptions.CourseInactiveException;
import br.com.alura.ProjetoAlura.exceptions.CourseNotFoundException;
import br.com.alura.ProjetoAlura.exceptions.DuplicateRegistrationException;
import br.com.alura.ProjetoAlura.exceptions.UserNotFoundException;
import br.com.alura.ProjetoAlura.user.User;
import br.com.alura.ProjetoAlura.user.UserRepository;

@Service
public class RegistrationService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;

    @Autowired
    public RegistrationService(CourseRepository courseRepository, UserRepository userRepository, RegistrationRepository registrationRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.registrationRepository = registrationRepository;
    }

    @Transactional
    public Registration registerStudent(NewRegistrationDTO newRegistration) {

        Course course = courseRepository.findByCode(newRegistration.getCourseCode())
                .orElseThrow(() -> new CourseNotFoundException("Course not found."));

        if (!course.isActive()) {
            throw new CourseInactiveException("The course is not active.");
        }

        User user = userRepository.findByEmail(newRegistration.getStudentEmail())
                .orElseThrow(() -> new UserNotFoundException("Student not found."));

        if (registrationRepository.existsByUserAndCourse(user, course)) {
            throw new DuplicateRegistrationException("Student is already registered for this course.");
        }

        Registration registration = convertDtoToEntity(newRegistration, user, course);

        return registrationRepository.save(registration);
    }

    private Registration convertDtoToEntity(NewRegistrationDTO newRegistration, User user, Course course) {
        Registration registration = new Registration();
        registration.setUser(user);
        registration.setCourse(course);
        registration.setRegistrationDate(LocalDateTime.now());
        return registration;
    }
}
