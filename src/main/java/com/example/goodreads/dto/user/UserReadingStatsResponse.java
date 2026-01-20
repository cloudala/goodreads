package com.example.goodreads.dto.user;

public class UserReadingStatsResponse {
    private Long userId;
    private Long totalBooksRead;
    private Long booksReadThisYear;
    private Long booksReadThisMonth;
    private Long currentlyReading;

    public UserReadingStatsResponse(Long totalBooksRead, Long booksReadThisYear, Long booksReadThisMonth, Long currentlyReading) {
        this.totalBooksRead = totalBooksRead;
        this.booksReadThisYear = booksReadThisYear;
        this.booksReadThisMonth = booksReadThisMonth;
        this.currentlyReading = currentlyReading;
    }

    public Long getTotalBooksRead() {
        return totalBooksRead;
    }

    public void setTotalBooksRead(Long totalBooksRead) {
        this.totalBooksRead = totalBooksRead;
    }

    public Long getBooksReadThisYear() {
        return booksReadThisYear;
    }

    public void setBooksReadThisYear(Long booksReadThisYear) {
        this.booksReadThisYear = booksReadThisYear;
    }

    public Long getBooksReadThisMonth() {
        return booksReadThisMonth;
    }

    public void setBooksReadThisMonth(Long booksReadThisMonth) {
        this.booksReadThisMonth = booksReadThisMonth;
    }

    public Long getCurrentlyReading() {
        return currentlyReading;
    }

    public void setCurrentlyReading(Long currentlyReading) {
        this.currentlyReading = currentlyReading;
    }
}
