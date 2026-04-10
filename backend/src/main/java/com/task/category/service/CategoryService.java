package com.task.category.service;

import com.task.category.entity.Category;
import com.task.category.repository.CategoryRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository repo;

    public CategoryService(CategoryRepository repo) {
        this.repo = repo;
    }

    public Category createCategory(Category category) {
    	System.out.println("ID: " + category.getCategoryId());
        System.out.println("Name: " + category.getCategoryName());
        return repo.save(category);
    }

    public List<Category> getAllCategories() {
        return repo.findAll();
    }
}