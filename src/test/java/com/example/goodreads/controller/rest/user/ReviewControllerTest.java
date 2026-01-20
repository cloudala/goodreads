package com.example.goodreads.controller.rest.user;

import com.example.goodreads.config.JwtAuthenticationFilter;
import com.example.goodreads.dto.review.ReviewRequest;
import com.example.goodreads.dto.review.ReviewResponse;
import com.example.goodreads.service.CustomUserDetailsService;
import com.example.goodreads.service.user.ReviewService;
import com.example.goodreads.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest(
//        controllers = ReviewController.class,
//        excludeAutoConfiguration = {
//                SecurityAutoConfiguration.class,
//                SecurityFilterAutoConfiguration.class
//        },
//        excludeFilters = @ComponentScan.Filter(
//                type = FilterType.ASSIGNABLE_TYPE,
//                classes = JwtAuthenticationFilter.class
//        )
//)
//@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = ReviewController.class)
@AutoConfigureMockMvc
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void testGetBookReviews() throws Exception {
        when(reviewService.getBookReviews(1L)).thenReturn(
                List.of(
                        new ReviewResponse(
                                1L,
                                5,
                                "Great book!",
                                "user1",
                                LocalDateTime.now()
                        )
                )
        );

        mockMvc.perform(get("/api/books/1/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

//    @Test
//    @WithMockUser(username = "alice")
//    void testAddReview() throws Exception {
//        ReviewRequest request = new ReviewRequest(5, "Amazing book!");
//
//        when(reviewService.addReview(eq("alice"), eq(1L), any()))
//                .thenReturn(
//                        new ReviewResponse(
//                                1L, 5, "Amazing book!", "alice", LocalDateTime.now()
//                        )
//                );
//
//        mockMvc.perform(post("/api/books/1/reviews")
//                        .contentType("application/json")
//                        .content(new ObjectMapper().writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.username").value("alice"));
//    }
}



