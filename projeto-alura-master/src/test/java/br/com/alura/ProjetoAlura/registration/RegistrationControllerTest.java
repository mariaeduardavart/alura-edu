package br.com.alura.ProjetoAlura.registration;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.alura.ProjetoAlura.exceptions.CourseInactiveException;
import br.com.alura.ProjetoAlura.exceptions.CourseNotFoundException;
import br.com.alura.ProjetoAlura.exceptions.DuplicateRegistrationException;
import br.com.alura.ProjetoAlura.exceptions.UserNotFoundException;

@WebMvcTest(RegistrationController.class)
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegistrationService registrationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createRegistration_shouldReturnNotFoundWhenCourseDoesNotExist() throws Exception {
        NewRegistrationDTO newRegistrationDTO = new NewRegistrationDTO();
        newRegistrationDTO.setCourseCode("invalidCourse");
        newRegistrationDTO.setStudentEmail("student@example.com");

        when(registrationService.registerStudent(any(NewRegistrationDTO.class)))
                .thenThrow(new CourseNotFoundException("Course not found."));

        mockMvc.perform(post("/registration/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRegistrationDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Course not found."));
    }

    @Test
    void createRegistration_shouldReturnBadRequestWhenCourseIsInactive() throws Exception {
        NewRegistrationDTO newRegistrationDTO = new NewRegistrationDTO();
        newRegistrationDTO.setCourseCode("inactiveCourse");
        newRegistrationDTO.setStudentEmail("student@example.com");

        when(registrationService.registerStudent(any(NewRegistrationDTO.class)))
                .thenThrow(new CourseInactiveException("The course is not active."));

        mockMvc.perform(post("/registration/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRegistrationDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("The course is not active."));
    }

    @Test
    void createRegistration_shouldReturnNotFoundWhenStudentDoesNotExist() throws Exception {
        NewRegistrationDTO newRegistrationDTO = new NewRegistrationDTO();
        newRegistrationDTO.setCourseCode("validCourse");
        newRegistrationDTO.setStudentEmail("invalid@example.com");

        when(registrationService.registerStudent(any(NewRegistrationDTO.class)))
                .thenThrow(new UserNotFoundException("Student not found."));

        mockMvc.perform(post("/registration/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRegistrationDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Student not found."));
    }

    @Test
    void createRegistration_shouldReturnBadRequestWhenStudentAlreadyRegistered() throws Exception {
        NewRegistrationDTO newRegistrationDTO = new NewRegistrationDTO();
        newRegistrationDTO.setCourseCode("validCourse");
        newRegistrationDTO.setStudentEmail("student@example.com");

        when(registrationService.registerStudent(any(NewRegistrationDTO.class)))
                .thenThrow(new DuplicateRegistrationException("Student is already registered for this course."));

        mockMvc.perform(post("/registration/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRegistrationDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Student is already registered for this course."));
    }

    @Test
    void createRegistration_shouldReturnCreatedWhenRegistrationIsSuccessful() throws Exception {
        NewRegistrationDTO newRegistrationDTO = new NewRegistrationDTO();
        newRegistrationDTO.setCourseCode("validCourse");
        newRegistrationDTO.setStudentEmail("student@example.com");

        Registration createdRegistration = new Registration();

        when(registrationService.registerStudent(any(NewRegistrationDTO.class))).thenReturn(createdRegistration);

        mockMvc.perform(post("/registration/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRegistrationDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void report_shouldReturnListOfMostPopularCourses() throws Exception {
        RegistrationReportItem reportItem1 = new RegistrationReportItem("Java for Beginners", "java", "John Doe", "john.doe@example.com", 10L);
        RegistrationReportItem reportItem2 = new RegistrationReportItem("Spring for Beginners", "spring", "John Doe", "john.doe@example.com", 9L);
        List<RegistrationReportItem> reportItems = Arrays.asList(reportItem1, reportItem2);

        when(registrationService.getMostPopularCourses()).thenReturn(reportItems);

        mockMvc.perform(get("/registration/report")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseName").value("Java for Beginners"))
                .andExpect(jsonPath("$[0].instructorName").value("John Doe"))
                .andExpect(jsonPath("$[0].totalRegistrations").value(10))
                .andExpect(jsonPath("$[1].courseName").value("Spring for Beginners"))
                .andExpect(jsonPath("$[1].totalRegistrations").value(9));
    }
}
