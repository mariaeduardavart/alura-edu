package br.com.alura.ProjetoAlura.course;

import java.time.LocalDateTime;

import br.com.alura.ProjetoAlura.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    @Size(min = 4, max = 10)
    @Pattern(
            regexp = "^(?!-)[a-zA-Z]+(-[a-zA-Z]+)*(?<!-)$",
            message = "The code must contain only letters and hyphens, cannot start or end with a hyphen, and must not contain spaces or numbers."
    )
    @Column(unique = true)
    private String code;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    private String description;

    @Enumerated(EnumType.STRING)
    private CourseStatus status;

    private LocalDateTime inactiveDate;

    public boolean isActive() {
        return this.status == CourseStatus.ACTIVE;
    }

    public Course() {
    }

    public Course(String name, String code, User instructor, String description) {
        this.name = name;
        this.code = code;
        this.instructor = instructor;
        this.description = description;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getInstructor() {
        return instructor;
    }

    public void setInstructor(User instructor) {
        this.instructor = instructor;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }

    public LocalDateTime getInactiveDate() {
        return inactiveDate;
    }

    public void setInactiveDate(LocalDateTime inactiveDate) {
        this.inactiveDate = inactiveDate;
    }
}
