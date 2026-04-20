package com.task.category.service;

import com.task.category.entity.Category;
import com.task.category.repository.CategoryRepository;
import com.task.exception.BadRequestException;
import com.task.exception.CategoryNotFoundException;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository repo;

    public CategoryService(CategoryRepository repo) {
        this.repo = repo;
    }

   
    public Category createCategory(Category category) {

        Integer maxId = repo.findMaxId();

        Integer nextId = (maxId == null) ? 1 : maxId + 1;

        category.setCategoryId(nextId);

        return repo.save(category);
    }


    public List<Category> getAllCategories() {
        return repo.findAll(); // don't throw error for empty list
    }


    public Category getCategoryById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

   
    public Category updateCategory(Integer id, Category updatedCategory) {
        Category existing = repo.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (updatedCategory.getCategoryName() == null || updatedCategory.getCategoryName().trim().isEmpty()) {
            throw new BadRequestException("Category name cannot be empty");
        }

        existing.setCategoryName(updatedCategory.getCategoryName());

        return repo.save(existing);
    }


    public void deleteCategory(Integer id) {
        Category existing = repo.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        try {
            repo.delete(existing);
        } catch (Exception e) {
            throw new BadRequestException("Cannot delete category. It may be linked to tasks.");
        }
    }


    public List<Category> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("Search keyword cannot be empty");
        }
        return repo.findByCategoryNameContainingIgnoreCase(name);
    }

 
    public List<Category> getCategoriesByTaskId(Integer taskId) {
        return repo.findCategoriesByTaskId(taskId);
    }
}