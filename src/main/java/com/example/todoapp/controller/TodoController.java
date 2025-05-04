package com.example.todoapp.controller;

import com.example.todoapp.dto.TodoRequest;
import com.example.todoapp.model.Todo;
import com.example.todoapp.repository.TodoRepository;
import com.example.todoapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.example.todoapp.service.TodoService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.todoapp.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/todos")
public class TodoController {
    
    private static final Logger logger = LoggerFactory.getLogger(TodoController.class);
    
    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private TodoService todoService;

    @Autowired
    private UserRepository userRepository;

    // Get all todos
    @GetMapping
    public List<Todo> getMyTodos() {
        logger.info("GET request received - fetching all todos");

        // Check if user is authenticated
        if (SecurityContextHolder.getContext().getAuthentication() == null ||
            !SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            logger.warn("Unauthorized access attempt to todos endpoint");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");

        List<Todo> todos = todoRepository.findByUser(user);
        logger.info("Returning {} todos", todos.size());
        return todos;
    }
    
    // Create a new todo
    @PostMapping
    public Todo createTodo(@RequestBody @Valid TodoRequest todoRequest) {
        logger.info("POST request received - creating new todo: {}", todoRequest);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username);
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");

        Todo todo = new Todo(todoRequest.getTitle(), todoRequest.getDescription(), user);
        Todo savedTodo = todoRepository.save(todo);
        logger.info("Todo created with ID: {}", savedTodo.getId());
        return savedTodo;
    }
    
    // Update an existing todo
    @PutMapping("/{id}")
    public Todo updateTodo(@PathVariable Long id, @RequestBody Todo todoDetails) {
        logger.info("PUT request received - updating todo with ID: {}", id);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        
        // Verify the todo belongs to the current user
        Todo existingTodo = todoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found"));
        if (!existingTodo.getUser().getId().equals(user.getId())) {
            logger.warn("Unauthorized attempt to update todo ID: {} by user: {}", id, username);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to update this todo");
        }

        Todo updatedTodo = todoService.updateTodoWithValidation(id, todoDetails);
        logger.info("Todo updated successfully: {}", updatedTodo);
        return updatedTodo;
    }

    // Delete a todo
    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable Long id) {
        logger.info("DELETE request received - deleting todo with ID: {}", id);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        
        // Verify the todo belongs to the current user
        Todo todo = todoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found"));
        if (!todo.getUser().getId().equals(user.getId())) {
            logger.warn("Unauthorized attempt to delete todo ID: {} by user: {}", id, username);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to delete this todo");
        }

        todoRepository.deleteById(id);
        logger.info("Todo with ID: {} deleted successfully", id);
    }
}
