package br.com.alura.ProjetoAlura.course;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.alura.ProjetoAlura.exceptions.CourseNotFoundException;
import br.com.alura.ProjetoAlura.exceptions.ForbiddenOperationException;
import br.com.alura.ProjetoAlura.exceptions.InstructorNotFoundException;

@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCourse_shouldReturnNotFoundWhenInstructorDoesNotExist() throws Exception {

        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setName("Test Course");
        newCourseDTO.setCode("testcourse");
        newCourseDTO.setDescription("Test Course Description");
        newCourseDTO.setInstructorEmail("instructor_email@example.com");

        when(courseService.createCourse(any(NewCourseDTO.class)))
                .thenThrow(new InstructorNotFoundException("Instructor not found."));

        mockMvc.perform(post("/course/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Instructor not found."));
    }

    @Test
    void createCourse_shouldReturnCreatedWhenCourseIsValid() throws Exception {

        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setName("Test Course");
        newCourseDTO.setCode("testcourse");
        newCourseDTO.setDescription("Test Course Description");
        newCourseDTO.setInstructorEmail("instrutor2@example.com");

        Course createdCourse = new Course();
        createdCourse.setName("Test Course");
        createdCourse.setCode("testcourse");
        createdCourse.setDescription("Test Course Description.");

        when(courseService.createCourse(any(NewCourseDTO.class))).thenReturn(createdCourse);

        mockMvc.perform(post("/course/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(createdCourse.getCode()))
                .andExpect(jsonPath("$.name").value(createdCourse.getName()))
                .andExpect(jsonPath("$.description").value(createdCourse.getDescription()));

        verify(courseService).createCourse(any(NewCourseDTO.class));
    }

    @Test
    void createCourse_shouldReturnForbiddenWhenUserIsNotInstructor() throws Exception {

        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setName("Test Course");
        newCourseDTO.setCode("testcourse");
        newCourseDTO.setDescription("Test Course Description");
        newCourseDTO.setInstructorEmail("student_email@example.com");

        when(courseService.createCourse(any(NewCourseDTO.class)))
                .thenThrow(new ForbiddenOperationException("Operation not allowed."));

        mockMvc.perform(post("/course/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Operation not allowed."));
    }

    @Test
    void deactivateCourse_shouldReturnNotFoundWhenCourseDoesNotExist() throws Exception {
        String courseCode = "nonexistentcourse";

        doThrow(new CourseNotFoundException("Course not found."))
                .when(courseService).deactivateCourse(courseCode);

        mockMvc.perform(post("/course/{code}/inactive", courseCode)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Course not found."));
    }

    @Test
    void deactivateCourse_shouldReturnOkWhenCourseIsInactive() throws Exception {
        String courseCode = "java2023";

        doNothing().when(courseService).deactivateCourse(courseCode);

        mockMvc.perform(post("/course/{code}/inactive", courseCode)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
