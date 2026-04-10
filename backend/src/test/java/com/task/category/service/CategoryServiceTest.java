
package com.task.category.service;
import com.task.category.entity.Category;
import com.task.category.repository.CategoryRepository;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository repo;

    @InjectMocks
    private CategoryService service;

    @Test
    void testCreateCategory() {
        Category cat = new Category();
        cat.setCategoryName("Test");

        when(repo.save(any(Category.class))).thenReturn(cat);

        Category result = service.createCategory(cat);

        assertEquals("Test", result.getCategoryName());
    }
}