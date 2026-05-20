package com.seleebe.youowe.auth;

import lombok.Data;

@Data
public class JwtRequest {

  private String username;
  private String password;
}