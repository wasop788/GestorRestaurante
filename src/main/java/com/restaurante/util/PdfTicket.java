package com.restaurante.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.restaurante.model.LineaPedido;
import com.restaurante.model.Mesa;
import com.restaurante.model.Pedido;

import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfTicket {

    public static String generarTicket(Pedido pedido, Mesa mesa, List<LineaPedido> lineas) {
        String ruta = "ticket_mesa" + mesa.getNumero() + "_pedido" + pedido.getId() + ".pdf";

        try {
            Document doc = new Document(PageSize.A6);
            PdfWriter.getInstance(doc, new FileOutputStream(ruta));
            doc.open();

            // Fuentes
            Font fuenteTitulo = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Font fuenteNormal = new Font(Font.FontFamily.HELVETICA, 10);
            Font fuenteNegrita = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            Font fuentePequena = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC);

            // Cabecera
            Paragraph titulo = new Paragraph("RESTAURANTE", fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(titulo);

            Paragraph subtitulo = new Paragraph("Ticket de consumo", fuentePequena);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(subtitulo);

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

            // Línea separadora
            doc.add(new Chunk(new LineSeparator()));
            doc.add(new Paragraph(" "));

            // Tabla de líneas
            PdfPTable tabla = new PdfPTable(4);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{4f, 1f, 2f, 2f});

            // Cabecera tabla
            for (String cab : new String[]{"Producto", "Cant.", "P. Unit.", "Subtotal"}) {
                PdfPCell celda = new PdfPCell(new Phrase(cab, fuenteNegrita));
                celda.setBackgroundColor(BaseColor.LIGHT_GRAY);
                celda.setBorder(Rectangle.BOTTOM);
                celda.setPadding(4);
                tabla.addCell(celda);
            }

            // Filas de productos
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

            // Pie
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