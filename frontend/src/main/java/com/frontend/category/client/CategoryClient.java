package com.frontend.category.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.*;

import com.frontend.category.model.Category;

import java.util.List;


@HttpExchange("/api/categories")
public interface CategoryClient {

    @GetExchange("/all")
    List<Category> getAll();

    @GetExchange("/search/{id}")
    Category getById(@PathVariable("id") Integer id);

    @GetExchange("/search")
    List<Category> searchByName(@RequestParam String name);

    @GetExchange("/task/{taskId}")
    List<Category> getByTask(@PathVariable("taskId") Integer taskId);

    @PostExchange("/create")
    Category create(@RequestBody Category category);

    @PutExchange("/update/{id}")
    Category update(@PathVariable("id") Integer id, @RequestBody Category category);

    @DeleteExchange("/delete/{id}")
    void delete(@PathVariable("id") Integer id);
}