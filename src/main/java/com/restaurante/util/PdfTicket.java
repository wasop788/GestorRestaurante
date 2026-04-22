package com.restaurante.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.restaurante.dao.ConfiguracionDAO;
import com.restaurante.model.LineaPedido;
import com.restaurante.model.Mesa;
import com.restaurante.model.Pedido;

import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class PdfTicket {

    public static String generarTicket(Pedido pedido, Mesa mesa, List<LineaPedido> lineas) {
        String ruta = "ticket_mesa" + mesa.getNumero() + "_pedido" + pedido.getId() + ".pdf";

        ConfiguracionDAO configDAO = new ConfiguracionDAO();
        Map<String, String> config = configDAO.obtenerTodo();
        String nombreRestaurante = config.getOrDefault("nombre", "Restaurante");
        String direccion = config.getOrDefault("direccion", "");
        String telefono = config.getOrDefault("telefono", "");
        String cif = config.getOrDefault("cif", "");

        try {
            Document doc = new Document(PageSize.A6);
            PdfWriter.getInstance(doc, new FileOutputStream(ruta));
            doc.open();

            Font fuenteTitulo  = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Font fuenteNormal  = new Font(Font.FontFamily.HELVETICA, 10);
            Font fuenteNegrita = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            Font fuentePequena = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC);

            // Cabecera con datos del restaurante
            Paragraph titulo = new Paragraph(nombreRestaurante.toUpperCase(), fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(titulo);

            if (!direccion.isEmpty()) {
                Paragraph pDir = new Paragraph(direccion, fuentePequena);
                pDir.setAlignment(Element.ALIGN_CENTER);
                doc.add(pDir);
            }
            if (!telefono.isEmpty() || !cif.isEmpty()) {
                String linea = "";
                if (!telefono.isEmpty()) linea += "Tel: " + telefono;
                if (!cif.isEmpty()) linea += (linea.isEmpty() ? "" : "  |  ") + "CIF: " + cif;
                Paragraph pContacto = new Paragraph(linea, fuentePequena);
                pContacto.setAlignment(Element.ALIGN_CENTER);
                doc.add(pContacto);
            }

            doc.add(new Paragraph(" "));

            // Datos del pedido
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            doc.add(new Paragraph("Mesa: " + mesa.getNumero(), fuenteNormal));
            doc.add(new Paragraph("Pedido nº: " + pedido.getId(), fuenteNormal));
            if (pedido.getFechaApertura() != null) {
                doc.add(new Paragraph("Fecha: " + pedido.getFechaApertura().format(fmt), fuenteNormal));
            }
            doc.add(new Paragraph("Atendido por: " + Sesion.getUsuarioActual().getNombre(), fuenteNormal));

            doc.add(new Paragraph(" "));
            doc.add(new Chunk(new LineSeparator()));
            doc.add(new Paragraph(" "));

            // Tabla de líneas
            PdfPTable tabla = new PdfPTable(4);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{4f, 1f, 2f, 2f});

            for (String cab : new String[]{"Producto", "Cant.", "P. Unit.", "Subtotal"}) {
                PdfPCell celda = new PdfPCell(new Phrase(cab, fuenteNegrita));
                celda.setBackgroundColor(BaseColor.LIGHT_GRAY);
                celda.setBorder(Rectangle.BOTTOM);
                celda.setPadding(4);
                tabla.addCell(celda);
            }

            for (LineaPedido linea : lineas) {
                PdfPCell cNombre = new PdfPCell(new Phrase(linea.getNombreProducto(), fuenteNormal));
                PdfPCell cCant   = new PdfPCell(new Phrase(String.valueOf(linea.getCantidad()), fuenteNormal));
                PdfPCell cPrecio = new PdfPCell(new Phrase(String.format("%.2f€", linea.getPrecioUnitario()), fuenteNormal));
                PdfPCell cSub    = new PdfPCell(new Phrase(String.format("%.2f€", linea.getSubtotal()), fuenteNormal));

                for (PdfPCell c : new PdfPCell[]{cNombre, cCant, cPrecio, cSub}) {
                    c.setBorder(Rectangle.BOTTOM);
                    c.setPadding(4);
                }
                cCant.setHorizontalAlignment(Element.ALIGN_CENTER);
                cPrecio.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cSub.setHorizontalAlignment(Element.ALIGN_RIGHT);

                tabla.addCell(cNombre);
                tabla.addCell(cCant);
                tabla.addCell(cPrecio);
                tabla.addCell(cSub);
            }

            doc.add(tabla);
            doc.add(new Paragraph(" "));
            doc.add(new Chunk(new LineSeparator()));

            // Total
            Paragraph total = new Paragraph(
                    String.format("TOTAL: %.2f €", pedido.getTotal()), fuenteTitulo
            );
            total.setAlignment(Element.ALIGN_RIGHT);
            doc.add(total);

            doc.add(new Paragraph(" "));

            Paragraph pie = new Paragraph("¡Gracias por su visita!", fuentePequena);
            pie.setAlignment(Element.ALIGN_CENTER);
            doc.add(pie);

            doc.close();
            System.out.println("Ticket generado: " + ruta);

        } catch (Exception e) {
            System.err.println("Error al generar PDF: " + e.getMessage());
            return null;
        }

        return ruta;
    }
}