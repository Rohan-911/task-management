package com.frontend.taskservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {


    @GetMapping("/home")
    public String homePage() {
        return "taskservice/home";   
    }

    @GetMapping("/task")
    public String taskPage() {
        return "taskservice/task-endpoints";   
    }

    @GetMapping("/user")
    public String userPage() {
        return "taskservice/user-endpoints";   
    }

    @GetMapping("/project")
    public String projectPage() {
        return "taskservice/project-endpoints";   
    }
}