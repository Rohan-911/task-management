package com.frontend.category.controller;

import com.frontend.category.client.CategoryClient;
import com.frontend.category.client.NotificationClient;
import com.frontend.category.model.Category;
import com.frontend.category.model.Notification;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
@RequestMapping("/category")
public class CategoryPageController {

    private final CategoryClient categoryClient;
    private final NotificationClient notificationClient;
    private final com.frontend.category.client.AuthClient authClient;

    public CategoryPageController(CategoryClient categoryClient,
                                  NotificationClient notificationClient,
                                  com.frontend.category.client.AuthClient authClient) {
        this.categoryClient = categoryClient;
        this.notificationClient = notificationClient;
        this.authClient = authClient;
    }

    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public String handleTypeMismatch(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex, Model model) {
        model.addAttribute("result", "invalid input");
        return "CategoryService/result";
    }

    @PostMapping("/do-login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          jakarta.servlet.http.HttpServletRequest request,
                          org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            com.frontend.category.dto.LoginRequestDTO loginReq = new com.frontend.category.dto.LoginRequestDTO();
            loginReq.setUsername(username);
            loginReq.setPassword(password);
            com.frontend.category.dto.AuthResponseDTO authRes = authClient.login(loginReq);
            
            if (authRes != null && authRes.getToken() != null) {
                HttpSession session = request.getSession();
                session.setAttribute("jwtToken", authRes.getToken());
                session.setAttribute("USERNAME", username);
                
                // Set admin status based on roles
                boolean isAdmin = authRes.getRoles() != null && 
                                  authRes.getRoles().stream()
                                         .anyMatch(r -> r.equalsIgnoreCase("admin") || 
                                                        r.equalsIgnoreCase("ADMIN") || 
                                                        r.equalsIgnoreCase("ROLE_ADMIN"));
                session.setAttribute("isAdmin", isAdmin);
            } else {
                redirectAttributes.addFlashAttribute("loginError", "Unauthorized User: Invalid username or password.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loginError", "Unauthorized User: Login failed.");
        }
        return "redirect:/category/";
    }

    @GetMapping({"/logout", "/notification/logout"})
    public String logout(jakarta.servlet.http.HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/category/";
    }

    @GetMapping({"", "/", "/notification"})
    public String home() {
        return "CategoryService/Category";
    }

    @GetMapping("/categories/search")
    public String showCategorySearchForm(Model model, HttpSession session) {
        return showForm("categoryById", model, session);
    }

    @GetMapping("/categories/search/name")
    public String showCategorySearchNameForm(Model model, HttpSession session) {
        return showForm("categoryByName", model, session);
    }

    @GetMapping("/categories/task")
    public String showCategoryTaskForm(Model model, HttpSession session) {
        return showForm("categoryByTask", model, session);
    }

    @GetMapping("/categories/delete")
    public String showCategoryDeleteForm(Model model, HttpSession session) {
        return showForm("categoryDelete", model, session);
    }

    @GetMapping("/notifications/id")
    public String showNotificationIdForm(Model model, HttpSession session) {
        return showForm("notificationById", model, session);
    }

    @GetMapping("/notifications/user")
    public String showNotificationUserForm(Model model, HttpSession session) {
        return showForm("notificationByUser", model, session);
    }

    @GetMapping("/notifications/search")
    public String showNotificationSearchForm(Model model, HttpSession session) {
        return showForm("notificationSearch", model, session);
    }

    @GetMapping("/notifications/delete")
    public String showNotificationDeleteForm(Model model, HttpSession session) {
        return showForm("notificationDelete", model, session);
    }

    @GetMapping("/categories/create")
    public String showCategoryCreateForm(Model model, HttpSession session) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            model.addAttribute("result", "Access Denied: Only Admins can access the creation form.");
            return "CategoryService/result";
        }
        return showForm("categoryCreate", model, session);
    }

    @GetMapping("/notifications/create")
    public String showNotificationCreateForm(Model model, HttpSession session) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            model.addAttribute("result", "Access Denied: Only Admins can access the creation form.");
            return "CategoryService/result";
        }
        return showForm("notificationCreate", model, session);
    }

    @GetMapping("/form")
    public String showForm(@RequestParam String api, Model model, HttpSession session) {
        // Secure specific form requests
        if ((api.contains("Create") || api.contains("Delete")) && 
            !Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            model.addAttribute("result", "Access Denied: Admin privileges required for this form.");
            return "CategoryService/result";
        }
        model.addAttribute("method", "GET");
        model.addAttribute("isDynamicPath", false);
        
        switch (api) {
            case "categoryById":
                model.addAttribute("apiTitle", "Search Category By ID");
                model.addAttribute("apiPath", "GET /api/categories/search/{id}");
                model.addAttribute("actionUrl", "/category/categories/search/{id}");
                model.addAttribute("isDynamicPath", true);
                model.addAttribute("fields", List.of(new FormField("id", "Category ID", "number", "Enter ID")));
                break;
            case "categoryByName":
                model.addAttribute("apiTitle", "Search Category By Name");
                model.addAttribute("apiPath", "GET /api/categories/search/name/{name}");
                model.addAttribute("actionUrl", "/category/categories/search/name/{name}");
                model.addAttribute("isDynamicPath", true);
                model.addAttribute("fields", List.of(new FormField("name", "Category Name", "text", "Enter Name")));
                break;
            case "categoryByTask":
                model.addAttribute("apiTitle", "Search Category By Task");
                model.addAttribute("apiPath", "GET /api/categories/task/{taskId}");
                model.addAttribute("actionUrl", "/category/categories/task/{taskId}");
                model.addAttribute("isDynamicPath", true);
                model.addAttribute("fields", List.of(new FormField("taskId", "Task ID", "number", "Enter Task ID")));
                break;
            case "categoryDelete":
                model.addAttribute("apiTitle", "Delete Category");
                model.addAttribute("apiPath", "DELETE /api/categories/delete/{id}");
                model.addAttribute("actionUrl", "/category/categories/delete/{id}");
                model.addAttribute("isDynamicPath", true);
                model.addAttribute("fields", List.of(new FormField("id", "Category ID", "number", "Enter ID to Delete")));
                break;
            case "notificationById":
                model.addAttribute("apiTitle", "Search Notification By ID");
                model.addAttribute("apiPath", "GET /api/notifications/id/{id}");
                model.addAttribute("actionUrl", "/category/notifications/id/{id}");
                model.addAttribute("isDynamicPath", true);
                model.addAttribute("fields", List.of(new FormField("id", "Notification ID", "number", "Enter ID")));
                break;
            case "notificationByUser":
                model.addAttribute("apiTitle", "Search Notification By User");
                model.addAttribute("apiPath", "GET /api/notifications/user/{userId}");
                model.addAttribute("actionUrl", "/category/notifications/user/{userId}");
                model.addAttribute("isDynamicPath", true);
                model.addAttribute("fields", List.of(new FormField("userId", "User ID", "number", "Enter User ID")));
                break;
            case "notificationSearch":
                model.addAttribute("apiTitle", "Search Notification Text");
                model.addAttribute("apiPath", "GET /api/notifications/search/{text}");
                model.addAttribute("actionUrl", "/category/notifications/search/{text}");
                model.addAttribute("isDynamicPath", true);
                model.addAttribute("fields", List.of(new FormField("text", "Search Text", "text", "Enter Text")));
                break;
            case "notificationDelete":
                model.addAttribute("apiTitle", "Delete Notification");
                model.addAttribute("apiPath", "DELETE /api/notifications/delete/{id}");
                model.addAttribute("actionUrl", "/category/notifications/delete/{id}");
                model.addAttribute("isDynamicPath", true);
                model.addAttribute("fields", List.of(new FormField("id", "Notification ID", "number", "Enter ID to Delete")));
                break;
            case "categoryCreate":
                model.addAttribute("apiTitle", "Create Category");
                model.addAttribute("apiPath", "POST /api/categories/create");
                model.addAttribute("actionUrl", "/category/categories/create");
                model.addAttribute("method", "POST");
                model.addAttribute("fields", List.of(new FormField("categoryName", "Category Name", "text", "Enter New Category Name")));
                break;
            case "notificationCreate":
                model.addAttribute("apiTitle", "Create Notification");
                model.addAttribute("apiPath", "POST /api/notifications/create");
                model.addAttribute("actionUrl", "/category/notifications/create");
                model.addAttribute("method", "POST");
                model.addAttribute("fields", List.of(
                    new FormField("text", "Notification Text", "text", "Enter Message"),
                    new FormField("userId", "User ID", "number", "Enter User ID")
                ));
                break;
        }
        return "CategoryService/api-form";
    }

    public static class FormField {
        private String name;
        private String label;
        private String type;
        private String placeholder;
        public FormField(String name, String label, String type, String placeholder) {
            this.name = name; this.label = label; this.type = type; this.placeholder = placeholder;
        }
        public String getName() { return name; }
        public String getLabel() { return label; }
        public String getType() { return type; }
        public String getPlaceholder() { return placeholder; }
    }

   

    @GetMapping("/categories/all")
    public String getAllCategories(HttpSession session, @RequestParam(defaultValue = "0") int page, Model model) {
        if (session.getAttribute("jwtToken") == null) {
            model.addAttribute("result", "Access Denied: Please login first to access the API.");
            return "CategoryService/result";
        }
        List<Category> all = categoryClient.getAll();
        int pageSize = 5;
        int totalItems = all.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        
        int start = Math.min(page * pageSize, totalItems);
        int end = Math.min(start + pageSize, totalItems);
        
        model.addAttribute("result", all.subList(start, end));
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("baseUrl", "/category/categories/all");
        return "CategoryService/result";
    }

    @GetMapping("/categories/search/{id}")
    public String getCategoryById(HttpSession session, @PathVariable Integer id, Model model) {
        if (session.getAttribute("jwtToken") == null) {
            model.addAttribute("result", "Access Denied: Please login first to access the API.");
            return "CategoryService/result";
        }
        if (id < 0) {
            model.addAttribute("result", "negative nos not allowed");
            return "CategoryService/result";
        }
        try {
            Category cat = categoryClient.getById(id);
            if (cat == null) throw new RuntimeException();
            model.addAttribute("result", List.of(cat));
        } catch (Exception e) {
            model.addAttribute("result", "Invalid id. Its not in the database");
        }
        return "CategoryService/result";
    }

    @GetMapping("/categories/search/name/{name}")
    public String searchByName(HttpSession session, @PathVariable String name, Model model) {
        if (session.getAttribute("jwtToken") == null) {
            model.addAttribute("result", "Access Denied: Please login first to access the API.");
            return "CategoryService/result";
        }
        try {
            List<Category> cats = categoryClient.searchByName(name);
            if (cats == null || cats.isEmpty()) throw new RuntimeException();
            model.addAttribute("result", cats);
        } catch (Exception e) {
            model.addAttribute("result", "Not a valid category. Its not in the database");
        }
        return "CategoryService/result";
    }

    @GetMapping("/categories/task/{taskId}")
    public String getByTask(HttpSession session, @PathVariable Integer taskId, Model model) {
        if (session.getAttribute("jwtToken") == null) {
            model.addAttribute("result", "Access Denied: Please login first to access the API.");
            return "CategoryService/result";
        }
        if (taskId < 0) {
            model.addAttribute("result", "negative nos not allowed");
            return "CategoryService/result";
        }
        try {
            List<Category> cats = categoryClient.getByTask(taskId);
            if (cats == null || cats.isEmpty()) throw new RuntimeException();
            model.addAttribute("result", cats);
        } catch (Exception e) {
            model.addAttribute("result", "Invalid id. Its not in the database");
        }
        return "CategoryService/result";
    }

    @PostMapping("/categories/create")
    public String createCategory(HttpSession session, @ModelAttribute Category category, Model model) {
        if (session.getAttribute("jwtToken") == null) {
            model.addAttribute("result", "Access Denied: Please login first to access the API.");
            return "CategoryService/result";
        }
        
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            model.addAttribute("result", "Access Denied: Only Admins can create categories.");
            return "CategoryService/result";
        }
        
        if (category.getCategoryName() != null && category.getCategoryName().matches(".*\\d.*")) {
            model.addAttribute("result", "invalid input");
            return "CategoryService/result";
        }
        
        try {
            List<Category> allCats = categoryClient.getAll();
            if (allCats != null) {
                boolean exists = allCats.stream()
                        .anyMatch(c -> c.getCategoryName().trim().equalsIgnoreCase(category.getCategoryName().trim()));
                if (exists) {
                    model.addAttribute("result", "Category already exist");
                    return "CategoryService/result";
                }
            }
        } catch (Exception e) {
            // Proceed to create if query fails
        }

        try {
            categoryClient.create(category);
            model.addAttribute("result", "Category created Successfully");
            return "CategoryService/result";
        } catch (Exception e) {
            model.addAttribute("result", "Error creating category: " + e.getMessage());
            return "CategoryService/result";
        }
    }

    @PostMapping("/categories/update")
    public String updateCategory(HttpSession session, @RequestParam Integer id,
                                 @ModelAttribute Category category, Model model) {
        if (session.getAttribute("jwtToken") == null) {
            model.addAttribute("result", "Access Denied: Please login first to access the API.");
            return "CategoryService/result";
        }
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            model.addAttribute("result", "Access Denied: Only Admins can update categories.");
            return "CategoryService/result";
        }
        try {
            categoryClient.update(id, category);
            return "redirect:/category/";
        } catch (Exception e) {
            model.addAttribute("result", "Error updating category: " + e.getMessage());
            return "CategoryService/result";
        }
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(HttpSession session, @PathVariable Integer id, Model model) {
        if (session.getAttribute("jwtToken") == null) {
            model.addAttribute("result", "Access Denied: Please login first to access the API.");
            return "CategoryService/result";
        }
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            model.addAttribute("result", "Access Denied: Only Admins can delete categories.");
            return "CategoryService/result";
        }
        if (id < 0) {
            model.addAttribute("result", "negative nos not allowed");
            return "CategoryService/result";
        }
        try {
            categoryClient.delete(id);
            model.addAttribute("result", "The category has been removed");
            return "CategoryService/result";
        } catch (Exception e) {
            model.addAttribute("result", "Invalid id. Its not in the database");
            return "CategoryService/result";
        }
    }

    @GetMapping("/notifications/all")
    public String getAllNotifications(HttpSession session, @RequestParam(defaultValue = "0") int page, Model model) {
        if (session.getAttribute("jwtToken") == null) {
            model.addAttribute("result", "Access Denied: Please login first to access the API.");
            return "CategoryService/result";
        }
        List<Notification> all = notificationClient.getAll();
        int pageSize = 5;
        int totalItems = all.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        
        int start = Math.min(page * pageSize, totalItems);
        int end = Math.min(start + pageSize, totalItems);
        
        model.addAttribute("result", all.subList(start, end));
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("baseUrl", "/category/notifications/all");
        return "CategoryService/result";
    }

    @GetMapping("/notifications/id/{id}")
    public String getNotificationById(HttpSession session, @PathVariable Integer id, Model model) {
        if (session.getAttribute("jwtToken") == null) {
            model.addAttribute("result", "Access Denied: Please login first to access the API.");
            return "CategoryService/result";
        }
        if (id < 0) {
            model.addAttribute("result", "negative nos not allowed");
            return "CategoryService/result";
        }
        try {
            Notification notif = notificationClient.getById(id);
            if (notif == null) throw new RuntimeException();
            model.addAttribute("result", List.of(notif));
        } catch (Exception e) {
            model.addAttribute("result", "Invalid id. Its not in the database");
        }
        return "CategoryService/result";
    }

    @GetMapping("/notifications/user/{userId}")
    public String getByUser(HttpSession session, @PathVariable Integer userId, Model model) {
        if (session.getAttribute("jwtToken") == null) {
            model.addAttribute("result", "Access Denied: Please login first to access the API.");
            return "CategoryService/result";
        }
        if (userId < 0) {
            model.addAttribute("result", "negative nos not allowed");
            return "CategoryService/result";
        }
        try {
            List<Notification> notifs = notificationClient.getByUser(userId);
            if (notifs == null || notifs.isEmpty()) throw new RuntimeException();
            model.addAttribute("result", notifs);
        } catch (Exception e) {
            model.addAttribute("result", "Invalid id. Its not in the database");
        }
        return "CategoryService/result";
    }

    @GetMapping("/notifications/search/{text}")
    public String searchNotification(HttpSession session, @PathVariable String text, Model model) {
        if (session.getAttribute("jwtToken") == null) {
            model.addAttribute("result", "Access Denied: Please login first to access the API.");
            return "CategoryService/result";
        }
        try {
            List<Notification> notifs = notificationClient.search(text);
            if (notifs == null || notifs.isEmpty()) throw new RuntimeException();
            model.addAttribute("result", notifs);
        } catch (Exception e) {
            model.addAttribute("result", "Not a valid notification. Its not in the database");
        }
        return "CategoryService/result";
    }

    @GetMapping("/notifications/latest")
    public String latestNotifications(HttpSession session, Model model) {
        if (session.getAttribute("jwtToken") == null) {
            model.addAttribute("result", "Access Denied: Please login first to access the API.");
            return "CategoryService/result";
        }
        model.addAttribute("result", notificationClient.getLatest());
        return "CategoryService/result";
    }

    @PostMapping("/notifications/create")
    public String createNotification(HttpSession session, @ModelAttribute Notification notification, Model model) {
        if (session.getAttribute("jwtToken") == null) {
            model.addAttribute("result", "Access Denied: Please login first to access the API.");
            return "CategoryService/result";
        }
        try {
            notificationClient.create(notification);
            model.addAttribute("result", "Notification posted successfully");
            return "CategoryService/result";
        } catch (Exception e) {
            model.addAttribute("result", "Error sending notification: " + e.getMessage());
            return "CategoryService/result";
        }
    }

    @PostMapping("/notifications/update")
    public String updateNotification(HttpSession session, @RequestParam Integer id,
                                     @ModelAttribute Notification notification, Model model) {
        if (session.getAttribute("jwtToken") == null) {
            model.addAttribute("result", "Access Denied: Please login first to access the API.");
            return "CategoryService/result";
        }
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            model.addAttribute("result", "Access Denied: Only Admins can update notifications.");
            return "CategoryService/result";
        }
        notificationClient.update(id, notification);
        return "redirect:/category/";
    }

    @GetMapping("/notifications/delete/{id}")
    public String deleteNotification(HttpSession session, @PathVariable Integer id, Model model) {
        if (session.getAttribute("jwtToken") == null) {
            model.addAttribute("result", "Access Denied: Please login first to access the API.");
            return "CategoryService/result";
        }
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            model.addAttribute("result", "Access Denied: Only Admins can delete notifications.");
            return "CategoryService/result";
        }
        if (id < 0) {
            model.addAttribute("result", "negative nos not allowed");
            return "CategoryService/result";
        }
        try {
            notificationClient.delete(id);
            model.addAttribute("result", "The Notification has been deleted");
            return "CategoryService/result";
        } catch (Exception e) {
            model.addAttribute("result", "Invalid id. Its not in the database");
            return "CategoryService/result";
        }
    }
}
