package com.expensetracker.application.service;

import com.expensetracker.domain.model.Expense;
import com.expensetracker.domain.model.User;
import com.expensetracker.domain.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    // Add a new expense for a user
    public Expense addExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    // List all expenses for a user
    public List<Expense> getExpensesByUser(User user) {
        return expenseRepository.findByUser(user);
    }

    // Get a specific expense by ID
    public Optional<Expense> getExpenseById(Long id) {
        return expenseRepository.findById(id);
    }

    // Update an expense
    public Expense updateExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    // Delete an expense by ID
    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }
}
