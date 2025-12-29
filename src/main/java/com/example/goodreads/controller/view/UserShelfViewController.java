package com.example.goodreads.controller.view;

import com.example.goodreads.dto.shelf.ShelfRequest;
import com.example.goodreads.dto.shelf.ShelfResponse;
import com.example.goodreads.exception.ShelfNotFoundException;
import com.example.goodreads.service.user.ShelfService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/shelves")
public class UserShelfViewController {

    private final ShelfService shelfService;

    public UserShelfViewController(ShelfService shelfService) {
        this.shelfService = shelfService;
    }

    @GetMapping
    public String getUserShelves(Model model, Principal principal) {
        List<ShelfResponse> shelves =
                shelfService.getUserShelves(principal.getName());
        model.addAttribute("shelves", shelves);
        return "user/shelves/list";
    }

    @GetMapping("/add")
    public String addShelfView(Model model) {
        model.addAttribute("shelf", new ShelfRequest());
        return "user/shelves/add-form";
    }

    @PostMapping("/add")
    public String addShelf(@Valid @ModelAttribute("shelf") ShelfRequest request,
                           BindingResult bindingResult,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "user/shelves/add-form";
        }

        shelfService.addShelfToUser(principal.getName(), request);
        redirectAttributes.addFlashAttribute("message", "Shelf created!");
        return "redirect:/shelves";
    }

    @GetMapping("/edit/{id}")
    public String editShelfView(@PathVariable Long id,
                                Model model,
                                Principal principal) {

        ShelfResponse shelf =
                shelfService.getUserShelves(principal.getName()).stream()
                        .filter(s -> s.getId().equals(id))
                        .findFirst()
                        .orElseThrow(() -> new ShelfNotFoundException("Shelf with id " + id + " not found"));

        model.addAttribute("shelf", shelf);
        return "user/shelves/edit-form";
    }

    @PostMapping("/edit/{id}")
    public String editShelf(@PathVariable Long id,
                            @Valid @ModelAttribute("shelf") ShelfRequest request,
                            BindingResult bindingResult,
                            Principal principal,
                            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "user/shelves/edit-form";
        }

        shelfService.updateUserShelf(principal.getName(), id, request);
        redirectAttributes.addFlashAttribute("message", "Shelf updated!");
        return "redirect:/shelves";
    }

    @GetMapping("/delete/{id}")
    public String deleteShelf(@PathVariable Long id,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {

        shelfService.deleteUserShelf(principal.getName(), id);
        redirectAttributes.addFlashAttribute("message", "Shelf deleted!");
        return "redirect:/shelves";
    }
}
