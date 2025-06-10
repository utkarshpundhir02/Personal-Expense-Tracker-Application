package com.expensetracker.domain.model;
package com.expensetracker.api.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "incomes")
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    private String source;

    private LocalDate date;

    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;  // assuming User entity exists

    // Constructors, getters, setters

    public Income() {}

    public Income(BigDecimal amount, String source, LocalDate date, String notes, User user) {
        this.amount = amount;
        this.source = source;
        this.date = date;
        this.notes = notes;
        this.user = user;
    }

    // Getters and setters below
    // ...
}
