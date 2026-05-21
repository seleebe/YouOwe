package com.seleebe.youowe.group;

import com.seleebe.youowe.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

  boolean existsByAdmin(User admin);
}
