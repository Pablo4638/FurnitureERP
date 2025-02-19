package group2.projecte2.serveis.implementacio;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import group2.projecte2.model.Pagament;
import group2.projecte2.repositori.jpa.PagamentRepositori;
import group2.projecte2.serveis.PagamentServei;

@Service
public class PagamentServeiImpl implements PagamentServei {

  @Autowired
  PagamentRepositori pagamentRepositori;

  @Override
  public List<Pagament> obtenirTots() {
    return pagamentRepositori.findAll();
  }

  @Override
  public Pagament guardar(Pagament pagament) {
    return pagamentRepositori.save(pagament);
  }

  @Override
  public Optional<Pagament> obtenirPerId(Long id) {
    return pagamentRepositori.findById(id);
  }

  @Override
  public void eliminar(Long id) {
    pagamentRepositori.deleteById(id);
  }

  public List<Pagament> filtrarYOrdenar(String filtro, String valor, String orden) {
        // Map de comparadores basado en los campos del filtro
        Map<String, Comparator<Pagament>> comparadores = Map.of(
                "nom", Comparator.comparing(pagament -> pagament.getFactura().getComanda().getClient().getNom()),
                "datapagament", Comparator.comparing(Pagament::getData_pagament),
                "nfactura", Comparator.comparing(pagament -> pagament.getFactura().getId_factura()),
                "total", Comparator.comparing(Pagament::getTotal));

        // Obtén el comparador correspondiente o un valor predeterminado
        Comparator<Pagament> comparador = comparadores.getOrDefault(
                filtro != null ? filtro.toLowerCase() : "",
                Comparator.comparing(pagament -> pagament.getFactura().getComanda().getClient().getNom()));

        // Invierte el orden si es "desc"
        if ("desc".equalsIgnoreCase(orden)) {
            comparador = comparador.reversed();
        }

        // Filtra y ordena la lista
        return pagamentRepositori.findAll().stream()
                .filter(p -> {
                    // Si el filtro o valor es null, no se filtra por campo específico
                    if (filtro == null || valor == null || valor.isEmpty()) {
                        return true; // No aplica filtro si no hay valor
                    }

                    // Filtra solo en el campo que coincide con el filtro especificado
                    switch (filtro.toLowerCase()) {
                        case "nom":
                            return p.getFactura().getComanda().getClient().getNom().toLowerCase().contains(valor.toLowerCase());
                        case "datapagament":
                            return p.getData_pagament().toString().toLowerCase().contains(valor.toLowerCase());
                        case "nfactura":
                            return p.getFactura().toString().toLowerCase().contains(valor.toLowerCase());
                        case "total":
                            return p.getTotal().toString().toLowerCase().contains(valor.toLowerCase());
                        default:
                            return true; // Si el filtro no coincide, no aplica filtro
                    }
                })
                .sorted(comparador) // Ordena según el comparador seleccionado
                .collect(Collectors.toList()); // Recoge el resultado como lista
    }
}
