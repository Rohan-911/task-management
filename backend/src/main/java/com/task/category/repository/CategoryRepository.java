package com.task.category.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.task.category.entity.Category;


public interface CategoryRepository extends JpaRepository<Category, Integer> {

}