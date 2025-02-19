package group2.projecte2.model;

import group2.projecte2.model.Enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Users")
public class User {

	@Id
	@Column(name = "user_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "username", unique = true, nullable = false)
    private String username;
	private String password;

	@Enumerated(EnumType.STRING)
	private Role role;

	private boolean enabled;
	private boolean esNou;

	@OneToOne
	@JoinColumn(name = "id_empleat")
	private Empleat empleat;
}
