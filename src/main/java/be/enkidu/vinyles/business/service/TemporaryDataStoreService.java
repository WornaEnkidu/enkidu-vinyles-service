package be.enkidu.vinyles.business.service;

import static be.enkidu.vinyles.business.service.constant.ExcelColumnConstants.*;

import be.enkidu.vinyles.business.service.dto.AlbumDTO;
import be.enkidu.vinyles.business.service.dto.ArtisteDTO;
import be.enkidu.vinyles.business.service.dto.TitreDTO;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class TemporaryDataStoreService {

    public void saveData(List<ArtisteDTO> artistes, List<TitreDTO> titres, List<AlbumDTO> albums) {
        Workbook workbook = new XSSFWorkbook();

        // Créer les feuilles pour Artistes, Titres et Albums
        createArtistesSheet(workbook, artistes);
        createTitresSheet(workbook, titres);
        createAlbumsSheet(workbook, albums);

        String filePath = "./exported_data/vinyles_export.xlsx";
        try {
            Files.createDirectories(Paths.get("./exported_data"));
            FileOutputStream fileOut = new FileOutputStream(filePath);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createArtistesSheet(Workbook workbook, List<ArtisteDTO> artistes) {
        Sheet sheet = workbook.createSheet("Artistes");
        Row header = sheet.createRow(0);
        ARTISTE_COLUMNS.forEach((code, label) -> header.createCell(getPositionOfKey(ARTISTE_COLUMNS, code)).setCellValue(label));

        int rowNum = 1;
        for (ArtisteDTO artiste : artistes) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(getPositionOfKey(ARTISTE_COLUMNS, "ID")).setCellValue(artiste.getId());
            row.createCell(getPositionOfKey(ARTISTE_COLUMNS, "NOM")).setCellValue(artiste.getNom());
            row.createCell(getPositionOfKey(ARTISTE_COLUMNS, "PRENOM")).setCellValue(artiste.getPrenom());
            row
                .createCell(getPositionOfKey(ARTISTE_COLUMNS, "DATE_NAISSANCE"))
                .setCellValue(artiste.getDateNaissance() != null ? artiste.getDateNaissance().toString() : "");
            row
                .createCell(getPositionOfKey(ARTISTE_COLUMNS, "DATE_DECES"))
                .setCellValue(artiste.getDateDeces() != null ? artiste.getDateDeces().toString() : "");
        }
    }

    private void createTitresSheet(Workbook workbook, List<TitreDTO> titres) {
        Sheet sheet = workbook.createSheet("Titres");
        Row header = sheet.createRow(0);
        TITRE_COLUMNS.forEach((code, label) -> header.createCell(getPositionOfKey(TITRE_COLUMNS, code)).setCellValue(label));

        int rowNum = 1;
        for (TitreDTO titre : titres) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(getPositionOfKey(TITRE_COLUMNS, "ID")).setCellValue(titre.getId());
            row.createCell(getPositionOfKey(TITRE_COLUMNS, "NOM")).setCellValue(titre.getNom());
            row.createCell(getPositionOfKey(TITRE_COLUMNS, "DUREE")).setCellValue(titre.getDuree());
            row
                .createCell(getPositionOfKey(TITRE_COLUMNS, "ARTISTE_IDS"))
                .setCellValue(String.join(",", titre.getArtistesIds().stream().map(String::valueOf).toList()));
        }
    }

    private void createAlbumsSheet(Workbook workbook, List<AlbumDTO> albums) {
        Sheet sheet = workbook.createSheet("Albums");
        Row header = sheet.createRow(0);
        ALBUM_COLUMNS.forEach((code, label) -> header.createCell(getPositionOfKey(ALBUM_COLUMNS, code)).setCellValue(label));

        int rowNum = 1;
        for (AlbumDTO album : albums) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(getPositionOfKey(ALBUM_COLUMNS, "ID")).setCellValue(album.getId());
            row.createCell(getPositionOfKey(ALBUM_COLUMNS, "NOM")).setCellValue(album.getNom());
            row
                .createCell(getPositionOfKey(ALBUM_COLUMNS, "ARTISTE_IDS"))
                .setCellValue(String.join(",", album.getArtistesIds().stream().map(String::valueOf).toList()));
            row
                .createCell(getPositionOfKey(ALBUM_COLUMNS, "TITRE_IDS"))
                .setCellValue(String.join(",", album.getTitresIds().stream().map(String::valueOf).toList()));
            row.createCell(getPositionOfKey(ALBUM_COLUMNS, "TAILLE")).setCellValue(album.getTaille());
            row.createCell(getPositionOfKey(ALBUM_COLUMNS, "STATUS")).setCellValue(album.getStatus());
        }
    }
}
