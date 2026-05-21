package com.seleebe.youowe.user;

import lombok.Data;

@Data
public class CreateUserDto {

  private String username;

  private String paymentInfo;

  private String password;
}
