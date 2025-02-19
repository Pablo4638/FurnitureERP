package group2.projecte2.serveis.implementacio;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;

import group2.projecte2.model.Comanda;
import group2.projecte2.model.DetallComanda;
import group2.projecte2.model.Factura;
import group2.projecte2.model.Enums.EstatFactura;
import group2.projecte2.repositori.jpa.FacturaRepositori;
import group2.projecte2.serveis.ComandaServei;
import group2.projecte2.serveis.DetallComandaServei;
import group2.projecte2.serveis.FacturaServei;

@Service
public class FacturaServeiImpl implements FacturaServei {
    @Autowired
    private FacturaRepositori facturaRepositori;

    @Autowired
    private DetallComandaServei detallComandaServei;

    @Autowired
    private ComandaServei comandaServei;

    @Override
    public List<Factura> obtenirTots() {
        return facturaRepositori.findAll();
    }

    @Override
    public void guardar(Factura factura) {
        facturaRepositori.save(factura);
    }

    @Override
    public Optional<Factura> obtenirPerId(Long id) {
        return facturaRepositori.findById(id);
    }

    @Override
    public void eliminar(Long id) {
        facturaRepositori.deleteById(id);
    }

    @Override
    public void writeTextAt1(PdfCanvas canvas, String text, float x, float y) {
        if (text == null) {
            text = "";
        }
        try {
            PdfFont font = PdfFontFactory.createFont("Helvetica");
            canvas.beginText()
                    .setFontAndSize(font, 8)
                    .setColor(Color.BLACK, true)
                    .moveText(x, y)
                    .showText(text)
                    .endText();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeTextAt(PdfCanvas canvas, String text, float x, float y) {
        if (text == null) {
            text = "";
        }
        try {
            PdfFont font = PdfFontFactory.createFont("Helvetica");
            canvas.beginText()
                    .setFontAndSize(font, 10)
                    .setColor(Color.BLACK, true)
                    .moveText(x, y)
                    .showText(text)
                    .endText();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String generatePdfWithCoordinates(Long num) {
        Boolean existent = false;
        try {
            Path facturaPath = Paths.get("facturas", "Factura" + num + ".pdf");
            Resource resource = new UrlResource(facturaPath.toUri());
            if (resource.exists()) {
                existent = true;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al carregar la factura", e);
        }

        if (!existent) {
            String src = "facturas/plantilla1.pdf";
            String desti = "facturas/Factura" + num + ".pdf";
            BigDecimal totalAmbIva = new BigDecimal(0);
            try {
                File outputFile = new File(desti);
                outputFile.getParentFile().mkdirs();

                PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(desti));
                Document document = new Document(pdfDoc);

                List<DetallComanda> detallComanda = detallComandaServei.obtenirPerComanda(num);
                Comanda comanda = comandaServei.obtenirPerId(num).orElse(null);

                PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());

                writeTextAt(canvas, comanda.getId_comanda().toString(), 475, 700);
                writeTextAt(canvas, comanda.getData_comanda().toString(), 475, 680);
                writeTextAt(canvas, "Nom: " + comanda.getClient().getNom(), 3, 580);
                writeTextAt(canvas, "Direcció: " + comanda.getClient().getDireccio(), 3, 565);
                writeTextAt(canvas, "Telèfon: " + comanda.getClient().getTelefon(), 3, 550);
                writeTextAt(canvas, "Email: " + comanda.getClient().getEmail(), 3, 535);
                for (int i = 0; i < detallComanda.size(); i++) {
                    writeTextAt1(canvas, detallComanda.get(i).getProducte().getNom_producte(), 3, 479 - (i * 14));
                    writeTextAt1(canvas, detallComanda.get(i).getQuantitat().toString(), 298, 479 - (i * 14));
                    writeTextAt1(canvas, detallComanda.get(i).getProducte().getCategoriaProducte().getNom_categoria(),
                            400, 479 - (i * 14));
                    writeTextAt1(canvas, detallComanda.get(i).getProducte().getPreu().toString(), 355, 479 - (i * 14));
                    writeTextAt1(canvas, detallComanda.get(i).getProducte().getDescripcio(), 89, 479 - (i * 14));
                    writeTextAt1(canvas, detallComanda.get(i).getSubtotal().toString(), 540, 479 - (i * 14));
                }

                totalAmbIva = comanda.getTotal().multiply(new BigDecimal("0.21")).add(comanda.getTotal());

                writeTextAt(canvas, comanda.getTotal().toString() + "€", 490, 180);
                writeTextAt(canvas, "21%", 490, 167);
                writeTextAt(canvas, totalAmbIva.toString() + " €", 490, 152);
                document.close();
                LocalDate currentDate = LocalDate.now();
                Date dataF = Date.valueOf(currentDate);
                Factura fac = new Factura(comanda, dataF, totalAmbIva, EstatFactura.Pendent);
                facturaRepositori.save(fac);
                return "PDF Generat a: " + desti;
            } catch (Exception e) {
                e.printStackTrace();
                return "Error generando el PDF: " + e.getMessage();
            }
        }
        return "";
    }

    @Override
    public List<Double> obtenirVentasAnuales() {
        List<Double> ventasAnuales = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 3; i >= 0; i--) {
            int year = today.getYear() - i;
            LocalDate startOfYear = LocalDate.of(year, 1, 1);
            LocalDate endOfYear = LocalDate.of(year, 12, 31);

            Date startDate = Date.valueOf(startOfYear);
            Date endDate = Date.valueOf(endOfYear);

            List<Factura> factures = facturaRepositori.findFacturesByYear(startDate, endDate);

            double totalVentas = factures.stream()
                    .filter(factura -> factura.getEstat() == EstatFactura.Pagada)
                    .mapToDouble(factura -> factura.getTotal().doubleValue())
                    .sum();

            ventasAnuales.add(totalVentas);
        }

        return ventasAnuales;
    }

    @Override
    public List<Double> obtenirVentasMensuales(int year) {
        List<Double> ventasMensuales = new ArrayList<>();

        // Iterar sobre los 12 meses del año proporcionado
        for (int month = 1; month <= 12; month++) {
            LocalDate startOfMonth = LocalDate.of(year, month, 1); // Primer día del mes
            LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth()); // Último día del mes

            Date startDate = Date.valueOf(startOfMonth);
            Date endDate = Date.valueOf(endOfMonth);

            List<Factura> factures = facturaRepositori.findFacturesByYear(startDate, endDate);

            double totalVentas = factures.stream()
                    .filter(factura -> factura.getEstat() == EstatFactura.Pagada)
                    .mapToDouble(factura -> factura.getTotal().doubleValue())
                    .sum();

            ventasMensuales.add(totalVentas);

            // Debug para validar las fechas y los resultados
            System.out.println("Ventas totales (Pagadas) del mes " + month + " del año "
                    + year + ": " + totalVentas);
        }

        return ventasMensuales;
    }

    @Override
    public List<Double> obtenirVentasSemanales() {
        List<Double> ventasSemanales = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // Recorre las últimas 4 semanas
        for (int i = 3; i >= 0; i--) {
            LocalDate startOfWeek = today.minusWeeks(i).with(DayOfWeek.MONDAY); // Primer día de la semana
            LocalDate endOfWeek = startOfWeek.plusDays(6); // Último día de la semana

            Date startDate = Date.valueOf(startOfWeek);
            Date endDate = Date.valueOf(endOfWeek);

            List<Factura> factures = facturaRepositori.findFacturesByYear(startDate, endDate);

            double totalVentas = factures.stream()
                    .filter(factura -> factura.getEstat() == EstatFactura.Pagada)
                    .mapToDouble(factura -> factura.getTotal().doubleValue())
                    .sum();

            ventasSemanales.add(totalVentas);
        }

        return ventasSemanales;
    }

    @Override
    public List<Factura> obtenirFacturaPendent() {
        return facturaRepositori.findFacturaPendent();
    }

    public List<Factura> filtrarYOrdenar(String filtro, String valor, String orden) {
        // Map de comparadores basado en los campos del filtro
        Map<String, Comparator<Factura>> comparadores = Map.of(
                "nom", Comparator.comparing(factura -> factura.getComanda().getClient().getNom()),
                "datafactura", Comparator.comparing(Factura::getData_factura),
                "estat", Comparator.comparing(Factura::getEstat),
                "total", Comparator.comparing(Factura::getTotal));

        // Obtén el comparador correspondiente o un valor predeterminado
        Comparator<Factura> comparador = comparadores.getOrDefault(
                filtro != null ? filtro.toLowerCase() : "",
                Comparator.comparing(factura -> factura.getComanda().getClient().getNom()));

        // Invierte el orden si es "desc"
        if ("desc".equalsIgnoreCase(orden)) {
            comparador = comparador.reversed();
        }

        // Filtra y ordena la lista
        return facturaRepositori.findAll().stream()
                .filter(f -> {
                    // Si el filtro o valor es null, no se filtra por campo específico
                    if (filtro == null || valor == null || valor.isEmpty()) {
                        return true; // No aplica filtro si no hay valor
                    }

                    // Filtra solo en el campo que coincide con el filtro especificado
                    switch (filtro.toLowerCase()) {
                        case "nom":
                            return f.getComanda().getClient().getNom().toLowerCase().contains(valor.toLowerCase());
                        case "datafactura":
                            return f.getData_factura().toString().toLowerCase().contains(valor.toLowerCase());
                        case "estat":
                            return f.getEstat().toString().toLowerCase().contains(valor.toLowerCase());
                        case "total":
                            return f.getTotal().toString().toLowerCase().contains(valor.toLowerCase());
                        default:
                            return true; // Si el filtro no coincide, no aplica filtro
                    }
                })
                .sorted(comparador) // Ordena según el comparador seleccionado
                .collect(Collectors.toList()); // Recoge el resultado como lista
    }
}
