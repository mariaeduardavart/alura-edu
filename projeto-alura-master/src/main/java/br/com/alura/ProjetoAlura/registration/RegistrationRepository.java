package br.com.alura.ProjetoAlura.registration;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.alura.ProjetoAlura.course.Course;
import br.com.alura.ProjetoAlura.user.User;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    boolean existsByUserAndCourse(User user, Course course);

    List<Registration> findAllByCourse(Course course);

    @Query(value = "SELECT new br.com.alura.ProjetoAlura.registration.RegistrationReportItem("
            + "c.name, c.code, i.name, i.email, COUNT(r.id)) "
            + "FROM Registration r "
            + "JOIN r.course c "
            + "JOIN c.instructor i "
            + "GROUP BY c.id, i.id "
            + "ORDER BY COUNT(r.id) DESC")
    List<RegistrationReportItem> findMostPopularCourses();
}
