package com.seleebe.youowe.debt;

import com.seleebe.youowe.group.Group;
import com.seleebe.youowe.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "debts")
@Getter
@Setter
@NoArgsConstructor
public class Debt {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne
  @JoinColumn(name = "debtor_id")
  private User debtor;

  @ManyToOne
  @JoinColumn(name = "creditor_id")
  private User creditor;

  @ManyToOne
  @JoinColumn(name = "group_id")
  private Group group;

  private BigDecimal amount;
}
