package be.enkidu.vinyles.business.service;

import be.enkidu.vinyles.business.service.dto.AlbumDTO;
import be.enkidu.vinyles.business.service.dto.ArtisteDTO;
import be.enkidu.vinyles.business.service.dto.TitreDTO;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
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
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        Table albumTable = new Table(new float[] { 1, 3, 3, 1 })
            .setWidth(UnitValue.createPercentValue(100))
            .setBackgroundColor(new DeviceRgb(240, 240, 240)); // Couleur de fond légère

        albumTable.addHeaderCell(createStyledCell("N°", headerColor, true));
        albumTable.addHeaderCell(createStyledCell("Nom de l'album", headerColor, true));
        albumTable.addHeaderCell(createStyledCell("Artiste", headerColor, true));
        albumTable.addHeaderCell(createStyledCell("Prix", headerColor, true));

        int albumIndex = 1;
        for (AlbumDTO album : albums) {
            albumTable.addCell(createStyledCell(String.valueOf(albumIndex++), ColorConstants.WHITE, false));
            albumTable.addCell(createStyledCell(album.getNom(), ColorConstants.WHITE, false));
            String artistes = album.getArtistes() != null
                ? album.getArtistes().stream().map(ArtisteDTO::getNomArtiste).collect(Collectors.joining(", "))
                : "";
            albumTable.addCell(createStyledCell(artistes, ColorConstants.WHITE, false));
            albumTable.addCell(createStyledCell(formatPrice(album.getPrix()), ColorConstants.WHITE, false));
        }

        // Calculer le prix total des albums
        double totalPrix = albums.stream().mapToDouble(album -> album.getPrix() != null ? album.getPrix() : 0.0).sum();

        // Ajouter la ligne total au tableau des albums
        Cell totalLabelCell = createStyledCell("Total", new DeviceRgb(63, 81, 181), true, 3);
        albumTable.addCell(totalLabelCell);

        Cell totalPrixCell = createStyledCell(formatPrice(totalPrix), new DeviceRgb(63, 81, 181), true);
        albumTable.addCell(totalPrixCell);

        document.add(albumTable);

        // Détails pour chaque album
        for (AlbumDTO album : albums) {
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

            // Ajouter le titre de l'album
            document.add(
                new Paragraph(album.getNom())
                    .setFontSize(24)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(new DeviceRgb(63, 81, 181))
                    .setMarginBottom(10)
            ); // Espace sous le titre

            // Ajouter les artistes
            String artistes = album.getArtistes() != null
                ? album.getArtistes().stream().map(ArtisteDTO::getNomArtiste).collect(Collectors.joining(", "))
                : "Artiste inconnu";
            document.add(
                new Paragraph("Artistes : " + artistes).setFontSize(14).setTextAlignment(TextAlignment.CENTER).setMarginBottom(20)
            );

            Table albumDetailTable = new Table(new float[] { 1, 3 }); // 2 colonnes, première colonne pour l'image et deuxième pour les infos
            albumDetailTable.setWidth(UnitValue.createPercentValue(100));

            // Charger et ajouter l'image de l'album
            if (album.getImage() != null && !album.getImage().isEmpty()) {
                try {
                    ImageData imageData = ImageDataFactory.create(album.getImage());
                    Image albumImage = new Image(imageData);
                    albumImage.setMaxWidth(100);
                    albumImage.setHeight(100);
                    albumImage.setAutoScale(false);

                    // Ajouter l'image dans la première cellule de la table
                    Cell imageCell = new Cell().add(albumImage);
                    imageCell.setBorder(Border.NO_BORDER);
                    albumDetailTable.addCell(imageCell);
                } catch (Exception e) {
                    System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
                }
            } else {
                // Ajouter une cellule vide si l'image est manquante
                albumDetailTable.addCell(new Cell().setBorder(Border.NO_BORDER));
            }

            // Ajouter les informations de l'album dans un tableau d'information
            Table infoTable = new Table(2);
            infoTable.setWidth(UnitValue.createPercentValue(100));
            infoTable.addCell(createInfoCell("Status :", true));
            infoTable.addCell(createInfoCell(album.getStatus(), false));
            infoTable.addCell(createInfoCell("Format :", true));
            infoTable.addCell(createInfoCell(album.getTaille(), false));
            //            infoTable.addCell(createInfoCell("Date de sortie :", true));
            //            infoTable.addCell(createInfoCell("", false));
            infoTable.setMarginBottom(20); // Espace après le tableau

            // Ajouter le tableau d'informations dans la deuxième cellule de la table principale
            Cell infoCell = new Cell().add(infoTable);
            infoCell.setBorder(Border.NO_BORDER);
            albumDetailTable.addCell(infoCell);

            // Ajouter la table principale avec image et infos au document
            document.add(albumDetailTable);

            // Tableau pour les titres
            Paragraph trackHeader = new Paragraph("Titres de l'album")
                .setFontSize(16)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
            document.add(trackHeader);

            Table trackTable = new Table(new float[] { 1, 4, 2 })
                .setWidth(UnitValue.createPercentValue(100))
                .setBackgroundColor(new DeviceRgb(240, 240, 240))
                .setMarginBottom(20);

            trackTable.addHeaderCell(createStyledCell("N°", new DeviceRgb(63, 81, 181), true));
            trackTable.addHeaderCell(createStyledCell("Titre", new DeviceRgb(63, 81, 181), true));
            trackTable.addHeaderCell(createStyledCell("Durée", new DeviceRgb(63, 81, 181), true));

            int trackIndex = 1;
            for (TitreDTO titreDTO : album.getTitres()) {
                trackTable.addCell(createStyledCell(String.valueOf(trackIndex++), ColorConstants.WHITE, false));
                trackTable.addCell(createStyledCell(titreDTO.getNom(), ColorConstants.WHITE, false));
                trackTable.addCell(createStyledCell(formatDuration(titreDTO.getDuree()), ColorConstants.WHITE, false));
            }

            document.add(trackTable);
        }

        document.close();
    }

    private Cell createStyledCell(String content, Color bgColor, boolean isHeader) {
        return createStyledCell(content, bgColor, isHeader, 1);
    }

    private Cell createStyledCell(String content, Color bgColor, boolean isHeader, int colspan) {
        Cell cell = new Cell(1, colspan).add(new Paragraph(content));
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

    private Cell createInfoCell(String content, boolean isLabel) {
        Cell cell = new Cell().add(new Paragraph(content));
        cell.setBorder(Border.NO_BORDER);
        cell.setTextAlignment(TextAlignment.LEFT);
        if (isLabel) {
            cell.setFontColor(new DeviceRgb(63, 81, 181)).setBold();
        } else {
            cell.setFontColor(ColorConstants.BLACK);
        }
        return cell;
    }

    // Gestionnaire d'événements pour le header et footer
    private class HeaderFooterEventHandler implements IEventHandler {

        private final String formattedDate;

        public HeaderFooterEventHandler() {
            // Récupérer la date actuelle et la formater
            LocalDate date = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
            this.formattedDate = date.format(formatter);
        }

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
                    .showText("Enkidu - Catalogue des Albums | Date d'export : " + formattedDate)
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

    public static String formatDuration(Integer value) {
        if (value == null || value < 0) {
            return "00:00";
        }

        int minutes = value / 60;
        int seconds = value % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    public static String formatPrice(Double value) {
        if (value == null || value < 0) {
            return "0.00€";
        }

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(value) + "€";
    }
}
