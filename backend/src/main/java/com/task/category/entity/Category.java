package com.task.category.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;


@Entity
@Table(name = "Category")
public class Category {

	    @Id
	    
	    @Column(name = "CategoryID")
	    private Integer categoryId;   // ✅ FIXED (was Long)

	    @NotBlank(message = "Category name cannot be empty")
	    @Column(name = "CategoryName")
	    private String categoryName;
	    

	    // Getters & Setters
	    public Integer getCategoryId() {
	        return categoryId;
	    }

	    public void setCategoryId(Integer categoryId) {
	        this.categoryId = categoryId;
	    }

	    public String getCategoryName() {
	        return categoryName;
	    }

	    public void setCategoryName(String categoryName) {
	        this.categoryName = categoryName;
	    }
	}
