package com.seleebe.youowe.user;

import com.seleebe.youowe.group.Group;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String username;

  private String phoneNumber;

  private String password;

  @ManyToMany(mappedBy = "members")
  private Set<Group> groups = new HashSet<>();
}
