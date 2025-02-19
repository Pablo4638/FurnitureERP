package group2.projecte2.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Asistencia {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id_asistencia;

  @ManyToOne
  @JoinColumn(name = "id_empleat")
  private Empleat empleat;

  private Date data;
  private Date hora_entrada;
  private Date hora_sortida;
}
