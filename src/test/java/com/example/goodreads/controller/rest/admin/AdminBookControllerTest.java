package com.example.goodreads.controller.rest.admin;

import com.example.goodreads.dto.admin.book.AdminBookResponse;
import com.example.goodreads.dto.admin.book.AdminCreateBookRequest;
import com.example.goodreads.dto.admin.book.AdminUpdateBookRequest;
import com.example.goodreads.service.admin.AdminBookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminBookController.class)
class AdminBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

//    @Autowired
//    private ObjectMapper objectMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AdminBookService adminBookService;

    /* =========================
       GET /api/admin/books
       ========================= */
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnAllBooks() throws Exception {
        // given
        List<AdminBookResponse> books = List.of(
                new AdminBookResponse(1L, "1984", "George Orwell", "978-0451524935", 1949),
                new AdminBookResponse(2L, "Brave New World", "Aldous Huxley", "978-0060850524", 1932)
        );

        when(adminBookService.getAllBooks()).thenReturn(books);

        // when + then
        mockMvc.perform(get("/api/admin/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("1984"))
                .andExpect(jsonPath("$[1].author").value("Aldous Huxley"));

        verify(adminBookService, times(1)).getAllBooks();
    }

    /* =========================
       GET /api/admin/books/{id}
       ========================= */
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBookById() throws Exception {
        // given
        AdminBookResponse response =
                new AdminBookResponse(1L, "The Hobbit", "J.R.R. Tolkien", "978-0547928227", 1937);

        when(adminBookService.getBookById(1L)).thenReturn(response);

        // when + then
        mockMvc.perform(get("/api/admin/books/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("The Hobbit"))
                .andExpect(jsonPath("$.author").value("J.R.R. Tolkien"));

        verify(adminBookService).getBookById(1L);
    }

    /* =========================
       POST /api/admin/books
       ========================= */
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateBook() throws Exception {
        // given
        AdminCreateBookRequest request = new AdminCreateBookRequest();
        request.setTitle("Dune");
        request.setAuthor("Frank Herbert");

        AdminBookResponse response =
                new AdminBookResponse(10L, "Dune", "Frank Herbert", "978-0441172719", 1965);

        when(adminBookService.createBook(any(AdminCreateBookRequest.class)))
                .thenReturn(response);

        String json = objectMapper.writeValueAsString(request);

        // when + then
        mockMvc.perform(post("/api/admin/books")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.title").value("Dune"));

        verify(adminBookService).createBook(any(AdminCreateBookRequest.class));
    }

    /* =========================
       PUT /api/admin/books/{id}
       ========================= */
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateBook() throws Exception {
        // given
        AdminUpdateBookRequest request = new AdminUpdateBookRequest();
        request.setTitle("Dune Messiah");

        AdminBookResponse response =
                new AdminBookResponse(1L, "Dune Messiah", "Frank Herbert", "978-0441172696", 1969);

        when(adminBookService.updateBook(eq(1L), any(AdminUpdateBookRequest.class)))
                .thenReturn(response);

        String json = objectMapper.writeValueAsString(request);

        // when + then
        mockMvc.perform(put("/api/admin/books/{id}", 1L)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Dune Messiah"));

        verify(adminBookService).updateBook(eq(1L), any(AdminUpdateBookRequest.class));
    }

    /* =========================
       DELETE /api/admin/books/{id}
       ========================= */
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteBook() throws Exception {
        // given
        doNothing().when(adminBookService).deleteBook(5L);

        // when + then
        mockMvc.perform(delete("/api/admin/books/{id}", 5L))
                .andExpect(status().isNoContent());

        verify(adminBookService).deleteBook(5L);
    }

    /* =========================
       SECURITY – brak roli ADMIN
       ========================= */
    @Test
    @WithMockUser(roles = "USER")
    void shouldRejectAccessForNonAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/books"))
                .andExpect(status().isForbidden());

        verify(adminBookService, never()).getAllBooks();
    }
}
