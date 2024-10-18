package br.com.alura.ProjetoAlura.course;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.alura.ProjetoAlura.exceptions.CourseNotFoundException;
import br.com.alura.ProjetoAlura.exceptions.ForbiddenOperationException;
import br.com.alura.ProjetoAlura.exceptions.InstructorNotFoundException;
import br.com.alura.ProjetoAlura.user.Role;
import br.com.alura.ProjetoAlura.user.User;
import br.com.alura.ProjetoAlura.user.UserRepository;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CourseService(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Course createCourse(NewCourseDTO courseDTO) {

        User instructor = findInstructorByEmail(courseDTO.getInstructorEmail());
        validateInstructorRole(instructor);

        Course course = convertDtoToEntity(courseDTO, instructor);
        return courseRepository.save(course);
    }

    private User findInstructorByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new InstructorNotFoundException("Instructor not found with email: " + email));
    }

    private void validateInstructorRole(User instructor) {
        if (!instructor.getRole().equals(Role.INSTRUCTOR)) {
            throw new ForbiddenOperationException("Operation not allowed.");
        }
    }

    private Course convertDtoToEntity(NewCourseDTO courseDTO, User instructor) {
        Course course = new Course();
        course.setName(courseDTO.getName());
        course.setCode(courseDTO.getCode());
        course.setDescription(courseDTO.getDescription());
        course.setInstructor(instructor);
        course.setStatus(CourseStatus.ACTIVE);
        course.setInactiveDate(null);
        return course;
    }

    @Transactional
    public void deactivateCourse(String courseCode) {
        Course course = courseRepository.findByCode(courseCode)
                .orElseThrow(() -> new CourseNotFoundException("Course not found."));

        course.setStatus(CourseStatus.INACTIVE);
        course.setInactiveDate(LocalDateTime.now());
        courseRepository.save(course);
    }

}
