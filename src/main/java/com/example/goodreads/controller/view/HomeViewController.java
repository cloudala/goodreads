package com.example.goodreads.controller.view;

import com.example.goodreads.service.user.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeViewController {

    private final BookService bookService;

    public HomeViewController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("featuredBooks", bookService.getFeaturedBooks());
        model.addAttribute("recentBooks", bookService.getRecentBooks());
        return "index";
    }
}

