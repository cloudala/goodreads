package com.example.goodreads.service.user;

import com.example.goodreads.dto.user.UpdateUserRequest;
import com.example.goodreads.dto.user.UpdateUserResponse;
import com.example.goodreads.dto.user.UserReadingStatsResponse;
import com.example.goodreads.exception.UsernameNotFoundException;
import com.example.goodreads.model.ShelfType;
import com.example.goodreads.model.User;
import com.example.goodreads.repository.ShelfRepository;
import com.example.goodreads.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ShelfRepository shelfRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ShelfRepository shelfRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.shelfRepository = shelfRepository;
    }

    public User getCurrentUser(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public UpdateUserResponse updateOwnProfile(String username, UpdateUserRequest updateRequest) {
        User user = getCurrentUser(username);
        if (updateRequest.getUsername() != null) {
            user.setUsername(updateRequest.getUsername());
        }
        if (updateRequest.getEmail() != null) {
            user.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }
        userRepository.save(user);
        return new UpdateUserResponse(
                user.getUsername(),
                user.getEmail()
        );
    }

    public void deleteOwnAccount(String username) {
        User user = getCurrentUser(username);
        userRepository.delete(user);
    }

    public UserReadingStatsResponse getUserReadingStats(String username) {
        User user = getCurrentUser(username);
        long totalBooksRead = shelfRepository.countTotalBooksByShelfTypeAndUserId(ShelfType.READ, user.getId());
        long booksReadThisYear = shelfRepository.countBooksByShelfTypeAndDateAddedAfter(ShelfType.READ, user.getId(), java.time.LocalDate.now().withDayOfYear(1));
        long booksReadThisMonth = shelfRepository.countBooksByShelfTypeAndDateAddedAfter(ShelfType.READ, user.getId(), java.time.LocalDate.now().withDayOfMonth(1));
        long currentlyReading = shelfRepository.countTotalBooksByShelfTypeAndUserId(ShelfType.CURRENTLY_READING, user.getId());

        return new UserReadingStatsResponse(
            totalBooksRead,
            booksReadThisYear,
            booksReadThisMonth,
            currentlyReading
        );
    }
}
