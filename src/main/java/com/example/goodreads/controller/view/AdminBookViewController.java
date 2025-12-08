package com.example.goodreads.controller.view;

import com.example.goodreads.dto.admin.book.AdminBookResponse;
import com.example.goodreads.dto.admin.book.AdminCreateBookRequest;
import com.example.goodreads.dto.admin.book.AdminUpdateBookRequest;
import com.example.goodreads.service.admin.AdminBookService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/books")
public class AdminBookViewController {
    private final AdminBookService adminBookService;

    public AdminBookViewController(AdminBookService adminBookService) {
        this.adminBookService = adminBookService;
    }

    @GetMapping
    public String getAdminBooksView(Model model) {
        List<AdminBookResponse> books = adminBookService.getAllBooks();
        model.addAttribute("books", books);
        return "admin/books/list";
    }

    @GetMapping("/add")
    public String addAdminBookView(Model model) {
        model.addAttribute("book", new AdminCreateBookRequest());
        return "admin/books/add-form";
    }

    @PostMapping("/add")
    public String addAdminBook(@Valid @ModelAttribute("book") AdminCreateBookRequest adminCreateBookRequest,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/books/add-form";
        }

        adminBookService.createBook(adminCreateBookRequest);
        redirectAttributes.addFlashAttribute("message", "Book added successfully!");
        return "redirect:/admin/books";
    }

    @GetMapping("/edit/{id}")
    public String editAdminBookView(@PathVariable Long id, Model model) {
        AdminBookResponse book = adminBookService.getBookById(id);
        model.addAttribute("book", book);
        return "admin/books/edit-form";
    }

    @PostMapping("/edit/{id}")
    public String editAdminBook(@PathVariable Long id,
                                @Valid @ModelAttribute("book") AdminUpdateBookRequest adminUpdateBookRequest,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/books/edit-form";
        }
        adminBookService.updateBook(id, adminUpdateBookRequest);
        redirectAttributes.addFlashAttribute("message", "Book updated successfully!");
        return "redirect:/admin/books";
    }

    @GetMapping("/delete/{id}")
    public String deleteAdminBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminBookService.deleteBook(id);
        redirectAttributes.addFlashAttribute("message", "Book deleted successfully!");
        return "redirect:/admin/books";
    }
}
