package com.seleebe.youowe.expense;

import com.seleebe.youowe.group.Group;
import com.seleebe.youowe.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "expenses")
@Getter
@Setter
@NoArgsConstructor
public class Expense {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private BigDecimal amount;

  private String description;

  @ManyToOne
  @JoinColumn(name = "payer_id")
  private User payer;

  @ManyToOne
  @JoinColumn(name = "group_id")
  private Group group;
}
