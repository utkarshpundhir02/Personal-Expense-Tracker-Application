package com.expensetracker.application.service;

import com.expensetracker.api.domain.Income;
import com.expensetracker.api.domain.User;
import com.expensetracker.api.repository.IncomeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IncomeService {

    private final IncomeRepository incomeRepository;

    public IncomeService(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    public Income addIncome(Income income) {
        return incomeRepository.save(income);
    }

    public List<Income> getIncomesByUser(User user) {
        return incomeRepository.findByUser(user);
    }

    public Optional<Income> getIncomeById(Long id) {
        return incomeRepository.findById(id);
    }

    public void deleteIncome(Long id) {
        incomeRepository.deleteById(id);
    }

    public Income updateIncome(Income income) {
        return incomeRepository.save(income);
    }
}
