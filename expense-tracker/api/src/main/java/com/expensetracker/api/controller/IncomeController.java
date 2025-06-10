package com.expensetracker.api.controller;
package com.expensetracker.api.controller;

import com.expensetracker.api.domain.Income;
import com.expensetracker.api.domain.User;
import com.expensetracker.api.service.IncomeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/incomes")
public class IncomeController {
    @Aut
    private final IncomeService incomeService;

    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    @PostMapping
    public ResponseEntity<Income> addIncome(@RequestBody Income income, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        income.setUser(user);
        Income savedIncome = incomeService.addIncome(income);
        return ResponseEntity.ok(savedIncome);
    }

    @GetMapping
    public ResponseEntity<List<Income>> getAllIncomes(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Income> incomes = incomeService.getIncomesByUser(user);
        return ResponseEntity.ok(incomes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Income> getIncomeById(@PathVariable Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return incomeService.getIncomeById(id)
                .filter(income -> income.getUser().getId().equals(user.getId()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Income> updateIncome(@PathVariable Long id, @RequestBody Income incomeDetails, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        return incomeService.getIncomeById(id)
                .filter(income -> income.getUser().getId().equals(user.getId()))
                .map(existingIncome -> {
                    existingIncome.setAmount(incomeDetails.getAmount());
                    existingIncome.setSource(incomeDetails.getSource());
                    existingIncome.setDate(incomeDetails.getDate());
                    existingIncome.setNotes(incomeDetails.getNotes());
                    Income updatedIncome = incomeService.updateIncome(existingIncome);
                    return ResponseEntity.ok(updatedIncome);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return incomeService.getIncomeById(id)
                .filter(income -> income.getUser().getId().equals(user.getId()))
                .map(income -> {
                    incomeService.deleteIncome(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
