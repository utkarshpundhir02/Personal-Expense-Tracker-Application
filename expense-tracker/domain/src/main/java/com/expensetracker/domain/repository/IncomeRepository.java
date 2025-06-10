package com.expensetracker.domain.repository;

import com.expensetracker.api.domain.Income;
import com.expensetracker.api.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    List<Income> findByUser(User user);
}
