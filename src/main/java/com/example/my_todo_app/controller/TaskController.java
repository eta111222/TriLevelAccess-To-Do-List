package com.example.my_todo_app.controller;

import com.example.my_todo_app.model.Task;
import com.example.my_todo_app.model.User;
import com.example.my_todo_app.repository.TaskRepository;
import com.example.my_todo_app.repository.UserRepository;
import com.example.my_todo_app.service.TaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import java.util.List;


@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/currentUser")
    public User getCurrentUser(
            @RequestHeader("X-Username") String username, 
            @RequestHeader("X-Role") String role,
            @RequestHeader("X-CompanyId") Long companyId) {

        Optional<User> userOptional = userRepository.findByUsername(username);
    
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Long userId = user.getId();
            
            return new User(userId, username, role, companyId);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }
    

    @GetMapping
    public List<Task> getTasks(
            @RequestHeader("X-Username") String username, 
            @RequestHeader("X-Role") String role, 
            @RequestHeader("X-CompanyId") Long companyId) {

        User user = getCurrentUser(username, role, companyId);
        
        System.out.println("User: " + user);
        List<Task> tasks = taskService.getTasks(user);
        System.out.println("Tasks: " + tasks);
        return tasks;
        
    }

    @PostMapping
    public Task createTask(
            @RequestBody Task task,
            @RequestHeader("X-Username") String username, 
            @RequestHeader("X-Role") String role, 
            @RequestHeader("X-CompanyId") Long companyId) {

        User user = getCurrentUser(username, role, companyId);
        return taskService.createTask(task, user);
    }

    @GetMapping("/{id}")
    public Task getTaskById(
            @PathVariable Long id,
            @RequestHeader("X-Username") String username, 
            @RequestHeader("X-Role") String role, 
            @RequestHeader("X-CompanyId") Long companyId) {

        User user = getCurrentUser(username, role, companyId);
        return taskService.getTaskById(id, user);
    }

    @PutMapping("/{id}")
    public Task updateTask(
            @PathVariable Long id, 
            @RequestBody Task task,
            @RequestHeader("X-Username") String username, 
            @RequestHeader("X-Role") String role, 
            @RequestHeader("X-CompanyId") Long companyId) {

        User user = getCurrentUser(username, role, companyId);
        return taskService.updateTask(id, task, user);
    }
    
    @DeleteMapping("/{id}")
    public void deleteTask(
            @PathVariable Long id,
            @RequestHeader("X-Username") String username, 
            @RequestHeader("X-Role") String role, 
            @RequestHeader("X-CompanyId") Long companyId) {

        User user = getCurrentUser(username, role, companyId);
        taskService.deleteTask(id, user);
    }

    @DeleteMapping
    public void deleteAllTasks() {
        taskRepository.deleteAll();  
    }
}

