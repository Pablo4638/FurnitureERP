package group2.projecte2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CanviContrasenyaDTO {
  private String password;
  private String passwordConfirm;
}
