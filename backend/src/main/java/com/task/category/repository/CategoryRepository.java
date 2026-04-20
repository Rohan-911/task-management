package com.task.category.repository;

import com.task.category.entity.Category;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

   
    List<Category> findByCategoryNameContainingIgnoreCase(String name);

    @Query(value = "SELECT c.* FROM Category c " +
                   "JOIN TaskCategory tc ON c.CategoryID = tc.CategoryID " +
                   "WHERE tc.TaskID = :taskId",
           nativeQuery = true)
    List<Category> findCategoriesByTaskId(@Param("taskId") Integer taskId);
    @Query("SELECT MAX(c.categoryId) FROM Category c")
    Integer findMaxId();
}