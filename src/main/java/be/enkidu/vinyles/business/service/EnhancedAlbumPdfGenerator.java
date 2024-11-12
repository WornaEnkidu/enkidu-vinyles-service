package be.enkidu.vinyles.business.service;

import be.enkidu.vinyles.business.service.dto.AlbumDTO;
import be.enkidu.vinyles.business.service.dto.ArtisteDTO;
import be.enkidu.vinyles.business.service.dto.TitreDTO;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class EnhancedAlbumPdfGenerator {

    public void generateAlbumPdf(ByteArrayOutputStream dest, List<AlbumDTO> albums) {
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Header et footer uniformes sur chaque page
        pdf.addEventHandler(PdfDocumentEvent.START_PAGE, new HeaderFooterEventHandler());

        // Couleur personnalisée pour les éléments
        Color headerColor = new DeviceRgb(63, 81, 181); // Couleur bleu foncé

        // Titre principal
        Paragraph title = new Paragraph("Catalogue des Albums")
            .setFontSize(24)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
            .setFontColor(headerColor);
        document.add(title);

        document.add(new Paragraph(" ").setFontSize(10)); // Espace entre le titre et la liste

        // Liste des albums en table avec couleurs et style
        Table albumTable = new Table(new float[] { 1, 3, 3 })
            .setWidth(UnitValue.createPercentValue(100))
            .setBackgroundColor(new DeviceRgb(240, 240, 240)); // Couleur de fond légère

        albumTable.addHeaderCell(createStyledCell("N°", headerColor, true));
        albumTable.addHeaderCell(createStyledCell("Nom de l'album", headerColor, true));
        albumTable.addHeaderCell(createStyledCell("Artiste", headerColor, true));

        int albumIndex = 1;
        for (AlbumDTO album : albums) {
            albumTable.addCell(createStyledCell(String.valueOf(albumIndex++), ColorConstants.WHITE, false));
            albumTable.addCell(createStyledCell(album.getNom(), ColorConstants.WHITE, false));
            String artistes = album.getArtistes() != null
                ? album.getArtistes().stream().map(ArtisteDTO::getNomArtiste).collect(Collectors.joining(", "))
                : "";
            albumTable.addCell(createStyledCell(artistes, ColorConstants.WHITE, false));
        }

        document.add(albumTable);

        // Détails pour chaque album
        for (AlbumDTO album : albums) {
            pdf.addNewPage();
            document.add(
                new Paragraph(album.getNom()).setFontSize(20).setBold().setTextAlignment(TextAlignment.CENTER).setFontColor(headerColor)
            );

            String artistes = album.getArtistes() != null
                ? album.getArtistes().stream().map(ArtisteDTO::getNomArtiste).collect(Collectors.joining(", "))
                : "";
            document.add(new Paragraph("Artistes: " + artistes).setFontSize(12).setTextAlignment(TextAlignment.CENTER));

            // Tableau pour les titres
            Table trackTable = new Table(new float[] { 1, 4, 2 })
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(10)
                .setBackgroundColor(new DeviceRgb(230, 230, 250)); // Couleur plus claire pour la section

            trackTable.addHeaderCell(createStyledCell("N°", headerColor, true));
            trackTable.addHeaderCell(createStyledCell("Titre", headerColor, true));
            trackTable.addHeaderCell(createStyledCell("Durée", headerColor, true));

            int trackIndex = 1;
            for (TitreDTO titreDTO : album.getTitres()) {
                trackTable.addCell(createStyledCell(String.valueOf(trackIndex++), ColorConstants.WHITE, false));
                trackTable.addCell(createStyledCell(titreDTO.getNom(), ColorConstants.WHITE, false));
                trackTable.addCell(createStyledCell(String.valueOf(titreDTO.getDuree()), ColorConstants.WHITE, false));
            }
            document.add(trackTable);
        }

        document.close();
    }

    private Cell createStyledCell(String content, Color bgColor, boolean isHeader) {
        Cell cell = new Cell().add(new Paragraph(content));
        cell.setBackgroundColor(bgColor);
        cell.setBorder(Border.NO_BORDER);
        if (isHeader) {
            cell.setFontColor(ColorConstants.WHITE).setBold();
        } else {
            cell.setFontColor(ColorConstants.BLACK);
        }
        cell.setTextAlignment(TextAlignment.CENTER);
        return cell;
    }

    // Gestionnaire d'événements pour le header et footer
    private class HeaderFooterEventHandler implements IEventHandler {

        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdfDoc = docEvent.getDocument();
            PdfCanvas canvas = new PdfCanvas(docEvent.getPage());

            // Header
            float x = pdfDoc.getDefaultPageSize().getWidth() / 2;
            float headerY = pdfDoc.getDefaultPageSize().getTop() - 20;
            try {
                canvas
                    .beginText()
                    .setFontAndSize(PdfFontFactory.createFont(), 10)
                    .moveText(x - 60, headerY)
                    .showText("Enkidu - Catalogue des Albums")
                    .endText();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Footer avec numéro de page
            float footerY = pdfDoc.getDefaultPageSize().getBottom() + 20;
            int pageNumber = pdfDoc.getPageNumber(docEvent.getPage());
            canvas.beginText().moveText(x - 30, footerY).showText("Page " + pageNumber).endText();

            canvas.release();
        }
    }
}
