package com.frontend.category.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.*;

import com.frontend.category.model.Notification;

import java.util.List;


@HttpExchange("/api/notifications")
public interface NotificationClient {

    @GetExchange("/all")
    List<Notification> getAll();

    @GetExchange("/{id}")
    Notification getById(@PathVariable("id") Integer id);

    @GetExchange("/user/{userId}")
    List<Notification> getByUser(@PathVariable("userId") Integer userId);

    @GetExchange("/search")
    List<Notification> search(@RequestParam String text);

    @GetExchange("/latest")
    List<Notification> getLatest();

    @PostExchange("/create")
    Notification create(@RequestBody Notification notification);

    @PutExchange("/update/{id}")
    Notification update(@PathVariable("id") Integer id, @RequestBody Notification notification);

    @DeleteExchange("/delete/{id}")
    void delete(@PathVariable("id") Integer id);
}