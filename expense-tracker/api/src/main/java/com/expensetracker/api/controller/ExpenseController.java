package com.expensetracker.api.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expensetracker.application.service.ExpenseService;
import com.expensetracker.application.service.UserService;
import com.expensetracker.domain.model.Expense;
import com.expensetracker.domain.model.User;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final UserService userService;

    public ExpenseController(ExpenseService expenseService, UserService userService) {
        this.expenseService = expenseService;
        this.userService = userService;
    }

    // DTO for creating/updating Expense
    public static class ExpenseRequest {
        public BigDecimal amount;
        public String category;
        public String date;  // ISO String, e.g. "2025-06-08"
        public String notes;
    }

    // Helper method to get the authenticated User object
    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();  // Assuming email is username in token
        return userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping
    public ResponseEntity<?> addExpense(@RequestBody ExpenseRequest request, Authentication authentication) {
        User user = getCurrentUser(authentication);

        Expense expense = new Expense();
        expense.setAmount(request.amount);
        expense.setCategory(request.category);
        expense.setDate(LocalDate.parse(request.date));
        expense.setNotes(request.notes);
        expense.setUser(user);

        Expense savedExpense = expenseService.addExpense(expense);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedExpense);
    }

    @GetMapping
    public ResponseEntity<List<Expense>> getExpenses(Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<Expense> expenses = expenseService.getExpensesByUser(user);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getExpenseById(@PathVariable("id") Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Optional<Expense> optionalExpense = expenseService.getExpenseById(id);

        if (optionalExpense.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Expense not found");
        }

        Expense expense = optionalExpense.get();
        if (!expense.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        return ResponseEntity.ok(expense);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateExpense(@PathVariable("id") Long id, @RequestBody ExpenseRequest request, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Optional<Expense> optionalExpense = expenseService.getExpenseById(id);

        if (optionalExpense.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Expense not found");
        }

        Expense expense = optionalExpense.get();
        if (!expense.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        expense.setAmount(request.amount);
        expense.setCategory(request.category);
        expense.setDate(LocalDate.parse(request.date));
        expense.setNotes(request.notes);

        Expense updatedExpense = expenseService.updateExpense(expense);
        return ResponseEntity.ok(updatedExpense);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable("id") Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Optional<Expense> optionalExpense = expenseService.getExpenseById(id);

        if (optionalExpense.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Expense not found");
        }

        Expense expense = optionalExpense.get();
        if (!expense.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
