package group2.projecte2.model;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import group2.projecte2.model.Enums.Departament;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class Empleat {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id_empleat;

  private String nom;
  private BigDecimal salari;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date data_contractacio;

  @Column(unique = true, nullable = false) // El campo email debe ser único y no puede ser nulo
  private String email;

  private String telefon;
  @Enumerated(EnumType.STRING)
  private Departament departament;

  @Column(unique = true)
  private String chatId; // El chatId del usuario de Telegram

  @Column(name = "codigo_verificacion", length = 6)
  private String codigoVerificacion;

}