package com.task.category.service;

import com.task.category.entity.Category;
import com.task.category.entity.Notification;
import com.task.category.repository.CategoryRepository;
import com.task.category.repository.NotificationRepository;
import com.task.exception.BadRequestException;
import com.task.exception.CategoryNotFoundException;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {


    @Mock
    private CategoryRepository categoryRepo;

    @InjectMocks
    private CategoryService categoryService;


    @Mock
    private NotificationRepository notificationRepo;

    @InjectMocks
    private NotificationService notificationService;


    @Test
    void testCreateCategory() {
        Category cat = new Category();
        cat.setCategoryName("Dev");

        when(categoryRepo.findMaxId()).thenReturn(1);
        when(categoryRepo.save(any(Category.class))).thenReturn(cat);

        Category result = categoryService.createCategory(cat);

        assertEquals("Dev", result.getCategoryName());
    }

    @Test
    void testGetCategoryById_Found() {
        Category cat = new Category();
        cat.setCategoryId(1);
        cat.setCategoryName("Dev");

        when(categoryRepo.findById(1)).thenReturn(Optional.of(cat));

        Category result = categoryService.getCategoryById(1);

        assertEquals("Dev", result.getCategoryName());
    }

    @Test
    void testGetCategoryById_NotFound() {
        when(categoryRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.getCategoryById(1));
    }

    @Test
    void testUpdateCategory_Success() {
        Category existing = new Category();
        existing.setCategoryId(1);
        existing.setCategoryName("Old");

        Category updated = new Category();
        updated.setCategoryName("New");

        when(categoryRepo.findById(1)).thenReturn(Optional.of(existing));
        when(categoryRepo.save(any(Category.class))).thenReturn(existing);

        Category result = categoryService.updateCategory(1, updated);

        assertEquals("New", result.getCategoryName());
    }

    @Test
    void testUpdateCategory_EmptyName() {
        Category existing = new Category();
        existing.setCategoryId(1);

        Category updated = new Category();
        updated.setCategoryName(" ");

        when(categoryRepo.findById(1)).thenReturn(Optional.of(existing));

        assertThrows(BadRequestException.class,
                () -> categoryService.updateCategory(1, updated));
    }

    @Test
    void testDeleteCategory_Success() {
        Category cat = new Category();
        cat.setCategoryId(1);

        when(categoryRepo.findById(1)).thenReturn(Optional.of(cat));

        categoryService.deleteCategory(1);

        verify(categoryRepo).delete(cat);
    }

    @Test
    void testDeleteCategory_Exception() {
        Category cat = new Category();
        cat.setCategoryId(1);

        when(categoryRepo.findById(1)).thenReturn(Optional.of(cat));
        doThrow(new RuntimeException()).when(categoryRepo).delete(cat);

        assertThrows(BadRequestException.class,
                () -> categoryService.deleteCategory(1));
    }

    @Test
    void testSearchByName_Success() {
        when(categoryRepo.findByCategoryNameContainingIgnoreCase("dev"))
                .thenReturn(List.of(new Category()));

        assertEquals(1, categoryService.searchByName("dev").size());
    }

    @Test
    void testSearchByName_Empty() {
        assertThrows(BadRequestException.class,
                () -> categoryService.searchByName(" "));
    }

    @Test
    void testGetCategoriesByTaskId() {
        when(categoryRepo.findCategoriesByTaskId(1))
                .thenReturn(List.of(new Category()));

        assertEquals(1, categoryService.getCategoriesByTaskId(1).size());
    }

 

    @Test
    void testCreateNotification() {
        Notification n = new Notification();
        n.setText("Test");
        n.setUserId(1);

        when(notificationRepo.findMaxId()).thenReturn(1);
        when(notificationRepo.save(any(Notification.class))).thenReturn(n);

        Notification result = notificationService.createNotification(n);

        assertEquals("Test", result.getText());
    }

    @Test
    void testGetNotificationById_Found() {
        Notification n = new Notification();
        n.setNotificationId(1);
        n.setText("Hello");

        when(notificationRepo.findById(1)).thenReturn(Optional.of(n));

        Notification result = notificationService.getNotificationById(1);

        assertEquals("Hello", result.getText());
    }

    @Test
    void testGetNotificationById_NotFound() {
        when(notificationRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> notificationService.getNotificationById(1));
    }

    @Test
    void testUpdateNotification() {
        Notification existing = new Notification();
        existing.setNotificationId(1);
        existing.setText("Old");

        Notification updated = new Notification();
        updated.setText("New");
        updated.setUserId(2);

        when(notificationRepo.findById(1)).thenReturn(Optional.of(existing));
        when(notificationRepo.save(any(Notification.class))).thenReturn(existing);

        Notification result = notificationService.updateNotification(1, updated);

        assertEquals("New", result.getText());
        assertEquals(2, result.getUserId());
    }

    @Test
    void testDeleteNotification() {
        Notification existing = new Notification();
        existing.setNotificationId(1);
        existing.setText("Test");

        when(notificationRepo.findById(1)).thenReturn(Optional.of(existing));
        doNothing().when(notificationRepo).deleteById(1);

        notificationService.deleteNotification(1);

        verify(notificationRepo).deleteById(1);
    }

    @Test
    void testGetNotificationsByUserId() {
        when(notificationRepo.findByUserId(1))
                .thenReturn(List.of(new Notification()));

        assertEquals(1, notificationService.getNotificationsByUserId(1).size());
    }

    @Test
    void testSearchByText() {
        when(notificationRepo.findByTextContaining("task"))
                .thenReturn(List.of(new Notification()));

        assertEquals(1, notificationService.searchByText("task").size());
    }

    @Test
    void testGetLatestNotifications() {
        when(notificationRepo.findTop5ByOrderByCreatedAtDesc())
                .thenReturn(List.of(new Notification()));

        assertEquals(1, notificationService.getLatestNotifications().size());
    }
}