package com.seleebe.youowe.debt;

import com.seleebe.youowe.group.Group;
import com.seleebe.youowe.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Long> {

  boolean existsByGroupAndDebtor(Group group, User debtor);

  boolean existsByGroupAndCreditor(Group group, User creditor);

  Optional<Debt> findByGroupAndDebtorAndCreditor(Group group, User debtor, User creditor);

  List<Debt> findByGroup(Group group);

  boolean existsByGroup(Group group);

  List<Debt> findAllByDebtor(User debtor);

  List<Debt> findAllByCreditor(User creditor);

  boolean existsByDebtor(User debtor);

  boolean existsByCreditor(User creditor);
}
