package com.example.my_todo_app;

import com.example.my_todo_app.model.Task;
import com.example.my_todo_app.model.User;
import com.example.my_todo_app.repository.TaskRepository;
import com.example.my_todo_app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class MyTodoAppApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository; 

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();  

        User superUser = new User(1L, "superUser", "Super User", 1L);
        User companyAdmin = new User(2L, "companyAdmin", "Company-Admin", 1L);
        User standardUser = new User(3L, "standardUser", "Standard User", 1L);
        userRepository.save(superUser);
        userRepository.save(companyAdmin);
        userRepository.save(standardUser);

        Long standardUserId = userRepository.findByUsername("standardUser").get().getId();
        Long companyAdminId = userRepository.findByUsername("companyAdmin").get().getId();
        Long superUserId = userRepository.findByUsername("superUser").get().getId();

        Task task1 = new Task(1L, "Task 1", "Description 1", standardUserId, 1L); 
        Task task2 = new Task(1L, "Task 2", "Description 1", companyAdminId, 1L); 
        Task task3 = new Task(1L, "Task 3", "Description 1", superUserId, 2L); 

        taskRepository.save(task1);
        taskRepository.save(task2);
        taskRepository.save(task3);

    }

    @Test
    void testSuperUserAccess() throws Exception {
        mockMvc.perform(get("/tasks")
                .header("X-Username", "superUser")
                .header("X-Role", "Super User")
                .header("X-CompanyId", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Task 1"))
            .andExpect(jsonPath("$[1].title").value("Task 2"))
            .andExpect(jsonPath("$[2].title").value("Task 3")); 
    }

    @Test
    void testCompanyAdminAccess() throws Exception {
        mockMvc.perform(get("/tasks")
                .header("X-Username", "companyAdmin")
                .header("X-Role", "Company-Admin")
                .header("X-CompanyId", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Task 1"))
            .andExpect(jsonPath("$[1].title").value("Task 2")); 
    }

    @Test
    void testStandardUserAccess() throws Exception {
        mockMvc.perform(get("/tasks")
                .header("X-Username", "standardUser")
                .header("X-Role", "Standard User")
                .header("X-CompanyId", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Task 1")); 
    }
}
