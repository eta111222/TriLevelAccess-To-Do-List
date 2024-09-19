package com.example.my_todo_app.service;

import com.example.my_todo_app.model.Task;
import com.example.my_todo_app.model.User;
import com.example.my_todo_app.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public List<Task> getTasks(User user) {
        switch (user.getRole()) {
            case "Super User":
                return taskRepository.findAll();
            case "Company-Admin":
                return taskRepository.findByCompanyId(user.getCompanyId());
            case "Standard User":
                System.out.println("Standard User ID: " + user.getId());
                System.out.println("Standard User Role: " + user.getRole());
            
                return taskRepository.findByUserId(user.getId());
            default:
                throw new IllegalArgumentException("Invalid role");
        }
    }

    public Task createTask(Task task, User user) {
        task.setUserId(user.getId());
        task.setCompanyId(user.getCompanyId());
        return taskRepository.save(task);
    }

    public Task getTaskById(Long id, User user) {
        Task existingTask = taskRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Task not found"));
        
        if ("Super User".equals(user.getRole())) {
            return existingTask;
        } else if ("Company-Admin".equals(user.getRole())) {
            if (existingTask.getCompanyId().equals(user.getCompanyId())) {
                return existingTask;
            } else {
                throw new IllegalArgumentException("No access to this task");
            }
        } else if ("Standard User".equals(user.getRole())) {
            if (existingTask.getUserId().equals(user.getId())) {
                return existingTask;
            } else {
                throw new IllegalArgumentException("No access to this task");
            }
        } else {
            throw new IllegalArgumentException("Invalid role");
        }
    }

    public Task updateTask(Long id, Task task, User user) {
        Task existingTask = taskRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Task not found"));
        
        if ("Super User".equals(user.getRole())) {
            return taskRepository.save(task);
        } else if ("Company-Admin".equals(user.getRole())) {
            if (existingTask.getCompanyId().equals(user.getCompanyId())) {
                return taskRepository.save(task);
            } else {
                throw new IllegalArgumentException("No access to update this task");
            }
        } else if ("Standard User".equals(user.getRole())) {
            if (existingTask.getUserId().equals(user.getId())) {
                return taskRepository.save(task);
            } else {
                throw new IllegalArgumentException("No access to update this task");
            }
        } else {
            throw new IllegalArgumentException("Invalid role");
        }
    }

    public void deleteTask(Long id, User user) {
        Task existingTask = taskRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Task not found"));
        
        if ("Super User".equals(user.getRole())) {
            taskRepository.deleteById(id);
        } else if ("Company-Admin".equals(user.getRole())) {
            if (existingTask.getCompanyId().equals(user.getCompanyId())) {
                taskRepository.deleteById(id);
            } else {
                throw new IllegalArgumentException("No access to delete this task");
            }
        } else if ("Standard User".equals(user.getRole())) {
            if (existingTask.getUserId().equals(user.getId())) {
                taskRepository.deleteById(id);
            } else {
                throw new IllegalArgumentException("No access to delete this task");
            }
        } else {
            throw new IllegalArgumentException("Invalid role");
        }
    }
}
