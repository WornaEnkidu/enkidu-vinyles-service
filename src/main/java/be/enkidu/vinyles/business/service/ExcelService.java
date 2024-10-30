package be.enkidu.vinyles.business.service;

import static be.enkidu.vinyles.business.service.constant.ExcelColumnConstants.*;

import be.enkidu.vinyles.business.service.dto.AlbumDTO;
import be.enkidu.vinyles.business.service.dto.ArtisteDTO;
import be.enkidu.vinyles.business.service.dto.TitreDTO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ExcelService {

    private final ArtisteService artisteService;
    private final TitreService titreService;
    private final AlbumService albumService;
    private final TemporaryDataStoreService temporaryDataStoreService;

    public ExcelService(
        ArtisteService artisteService,
        TitreService titreService,
        AlbumService albumService,
        TemporaryDataStoreService temporaryDataStoreService
    ) {
        this.artisteService = artisteService;
        this.titreService = titreService;
        this.albumService = albumService;
        this.temporaryDataStoreService = temporaryDataStoreService;
    }

    public byte[] exportDataToExcel() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            createSheet(workbook, "Artistes", artisteService.exportArtistes());
            createSheet(workbook, "Titres", titreService.exportTitres());
            createSheet(workbook, "Albums", albumService.exportAlbums());

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    public void saveData(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);

            List<ArtisteDTO> artistes = readArtistesSheet(workbook.getSheet("Artistes"));
            List<TitreDTO> titres = readTitresSheet(workbook.getSheet("Titres"));
            List<AlbumDTO> albums = readAlbumsSheet(workbook.getSheet("Albums"));

            workbook.close();
            this.temporaryDataStoreService.saveData(artistes, titres, albums);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<ArtisteDTO> readArtistesSheet(Sheet sheet) {
        List<ArtisteDTO> artistes = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            ArtisteDTO artiste = new ArtisteDTO();
            artiste.setId(Long.parseLong(row.getCell(getPositionOfKey(ARTISTE_COLUMNS, "ID")).getStringCellValue()));
            artiste.setNom(row.getCell(getPositionOfKey(ARTISTE_COLUMNS, "Nom")).getStringCellValue());
            artiste.setPrenom(row.getCell(getPositionOfKey(ARTISTE_COLUMNS, "Prenom")).getStringCellValue());

            try {
                if (row.getCell(getPositionOfKey(ARTISTE_COLUMNS, "Date Naissance")) != null) {
                    String dateNaissanceStr = row.getCell(getPositionOfKey(ARTISTE_COLUMNS, "Date Naissance")).getStringCellValue().trim();
                    if (!dateNaissanceStr.isEmpty()) {
                        Date dateNaissance = dateFormat.parse(dateNaissanceStr);
                        artiste.setDateNaissance(dateNaissance);
                    }
                }
                if (row.getCell(getPositionOfKey(ARTISTE_COLUMNS, "Date Décès")) != null) {
                    String dateDecesStr = row.getCell(getPositionOfKey(ARTISTE_COLUMNS, "Date Décès")).getStringCellValue().trim();
                    if (!dateDecesStr.isEmpty()) {
                        Date dateDeces = dateFormat.parse(dateDecesStr);
                        artiste.setDateDeces(dateDeces);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            artistes.add(artiste);
        }
        return artistes;
    }

    private List<TitreDTO> readTitresSheet(Sheet sheet) {
        List<TitreDTO> titres = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            TitreDTO titre = new TitreDTO();
            titre.setId(Long.parseLong(row.getCell(getPositionOfKey(TITRE_COLUMNS, "ID")).getStringCellValue()));
            titre.setNom(row.getCell(getPositionOfKey(TITRE_COLUMNS, "Nom")).getStringCellValue());

            String dureeStr = row.getCell(getPositionOfKey(TITRE_COLUMNS, "Durée")).getStringCellValue();
            String[] parts = dureeStr.split(":");
            if (parts.length == 2) {
                try {
                    int minutes = Integer.parseInt(parts[0].trim());
                    int seconds = Integer.parseInt(parts[1].trim());
                    titre.setDuree(minutes * 60 + seconds); // Convertir en secondes
                } catch (NumberFormatException e) {
                    e.printStackTrace(); // Log en cas de format incorrect
                }
            }

            // Extraction des IDs des artistes
            String[] artistesIdsStr = row.getCell(getPositionOfKey(TITRE_COLUMNS, "Artistes IDs")).getStringCellValue().split(",");
            List<Long> artistesIds = new ArrayList<>();
            for (String idStr : artistesIdsStr) {
                artistesIds.add(Long.parseLong(idStr.trim()));
            }
            titre.setArtistesIds(artistesIds);

            titres.add(titre);
        }
        return titres;
    }

    private List<AlbumDTO> readAlbumsSheet(Sheet sheet) {
        List<AlbumDTO> albums = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            AlbumDTO album = new AlbumDTO();
            album.setId(Long.parseLong(row.getCell(getPositionOfKey(ALBUM_COLUMNS, "ID")).getStringCellValue()));
            album.setNom(row.getCell(getPositionOfKey(ALBUM_COLUMNS, "Nom")).getStringCellValue());

            // Extraction des IDs des artistes
            String[] artistesIdsStr = row.getCell(getPositionOfKey(ALBUM_COLUMNS, "Artiste IDs")).getStringCellValue().split(",");
            List<Long> artistesIds = new ArrayList<>();
            for (String idStr : artistesIdsStr) {
                artistesIds.add(Long.parseLong(idStr.trim()));
            }
            album.setArtistesIds(artistesIds);

            // Extraction des IDs des titres
            String[] titresIdsStr = row.getCell(getPositionOfKey(ALBUM_COLUMNS, "Titre IDs")).getStringCellValue().split(",");
            List<Long> titresIds = new ArrayList<>();
            for (String idStr : titresIdsStr) {
                titresIds.add(Long.parseLong(idStr.trim()));
            }
            album.setTitresIds(titresIds);

            album.setTaille(row.getCell(getPositionOfKey(ALBUM_COLUMNS, "Taille")).getStringCellValue());
            album.setStatus(row.getCell(getPositionOfKey(ALBUM_COLUMNS, "Status")).getStringCellValue());

            albums.add(album);
        }
        return albums;
    }

    private void createSheet(Workbook workbook, String sheetName, List<Map<String, String>> data) {
        Sheet sheet = workbook.createSheet(sheetName);
        if (data.isEmpty()) return;

        // Header row
        Row headerRow = sheet.createRow(0);
        int headerIndex = 0;
        for (String key : data.get(0).keySet()) {
            Cell cell = headerRow.createCell(headerIndex++);
            cell.setCellValue(key);
        }

        // Data rows
        int rowIndex = 1;
        for (Map<String, String> rowData : data) {
            Row row = sheet.createRow(rowIndex++);
            int cellIndex = 0;
            for (Object value : rowData.values()) {
                Cell cell = row.createCell(cellIndex++);
                cell.setCellValue(value != null ? value.toString() : "");
            }
        }
    }
}
