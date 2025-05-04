package com.example.todoapp.service;

import com.example.todoapp.model.Todo;
import com.example.todoapp.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TodoService {
    
    @Autowired
    private TodoRepository todoRepository;
    
    // Example of a complex function moved from controller
    public Todo updateTodoWithValidation(Long id, Todo todoDetails) {
        // Validate the todo
        if (todoDetails.getTitle() == null || todoDetails.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Todo title cannot be empty");
        }
        
        // Find the existing todo
        Todo todo = todoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));
        
        // Update fields
        todo.setTitle(todoDetails.getTitle());
        
        // Handle description specifically to preserve integer format if needed
        if (todoDetails.getDescription() != null) {
            // Option 1: If you want to keep it as a string but in a specific format
            // This preserves the integer appearance by removing any decimal part
            try {
                int descriptionAsInt = Integer.parseInt(todoDetails.getDescription());
                todo.setDescription(String.valueOf(descriptionAsInt));
            } catch (NumberFormatException e) {
                // If it's not a valid integer, use it as is
                todo.setDescription(todoDetails.getDescription());
            }
            
            // Option 2 (alternative): If you just want to use the description as is
            // todo.setDescription(todoDetails.getDescription());
        }
        
        todo.setDone(todoDetails.isDone());
        
        // Additional business logic can go here
        
        // Save and return
        return todoRepository.save(todo);
    }
}