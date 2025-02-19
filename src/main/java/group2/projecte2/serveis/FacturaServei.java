package group2.projecte2.serveis;

import java.util.List;
import java.util.Optional;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

import group2.projecte2.model.Factura;

public interface FacturaServei {
    List<Factura> obtenirTots();

    void guardar(Factura factura);

    Optional<Factura> obtenirPerId(Long id);

    void eliminar(Long id);

    void writeTextAt1(PdfCanvas canvas, String text, float x, float y);

    void writeTextAt(PdfCanvas canvas, String text, float x, float y);

    String generatePdfWithCoordinates(Long num);

    public List<Double> obtenirVentasAnuales();

    public List<Double> obtenirVentasMensuales(int year);

    public List<Double> obtenirVentasSemanales();

    List<Factura> obtenirFacturaPendent();

    List<Factura> filtrarYOrdenar(String filtro, String valor, String orden);
}
