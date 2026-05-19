package com.seleebe.youowe.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {

  private Long id;
  private String username;
  private String phoneNumber;
}
