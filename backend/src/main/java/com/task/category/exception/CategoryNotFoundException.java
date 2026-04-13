package com.task.category.exception;



public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Integer id) {
        super("Category not found with ID: " + id);
    }
}