package com.seleebe.youowe.group;

import com.seleebe.youowe.user.UserResponseDto;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupResponseDto {

  private Long id;
  private String name;
  private List<UserResponseDto> members;
}
