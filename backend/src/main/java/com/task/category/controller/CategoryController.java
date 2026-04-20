package com.task.category.controller;

import com.task.category.entity.Category;
import com.task.category.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }
    
    @PostMapping("/create")
    public Category createCategory(@Valid @RequestBody Category category) {
        return service.createCategory(category);
    }

   
    @GetMapping("/all")
    public List<Category> getAll() {
        return service.getAllCategories();
    }

  
    @GetMapping("/search/{id}")
    public Category getById(@PathVariable Integer id) {
        return service.getCategoryById(id);
    }

 
    
    @PutMapping("/update/{id}")
    public Category updateCategory(@PathVariable Integer id,
                                   @Valid @RequestBody Category category) {
        return service.updateCategory(id, category);
    }

  
    
    @DeleteMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Integer id) {
        service.deleteCategory(id);
        return "Category deleted successfully";
    }

   
    @GetMapping("/search")
    public List<Category> searchByName(@RequestParam String name) {
        return service.searchByName(name);
    }

  
    @GetMapping("/task/{taskId}")
    public List<Category> getCategoriesByTask(@PathVariable Integer taskId) {
        return service.getCategoriesByTaskId(taskId);
    }
}