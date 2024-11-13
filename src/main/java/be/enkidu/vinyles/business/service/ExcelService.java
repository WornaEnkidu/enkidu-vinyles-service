package be.enkidu.vinyles.business.service;

import static be.enkidu.vinyles.business.service.constant.ExcelColumnConstants.*;

import be.enkidu.vinyles.business.service.dto.AlbumFormDTO;
import be.enkidu.vinyles.business.service.dto.ArtisteDTO;
import be.enkidu.vinyles.business.service.dto.TitreDTO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
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
            createSheet(workbook, "Artistes", artisteService.exportArtistes(), ARTISTE_COLUMNS);
            createSheet(workbook, "Titres", titreService.exportTitres(), TITRE_COLUMNS);
            createSheet(workbook, "Albums", albumService.exportAlbums(), ALBUM_COLUMNS);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void saveData(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);

            List<ArtisteDTO> artistes = readArtistesSheet(workbook.getSheet("Artistes"));
            List<TitreDTO> titres = readTitresSheet(workbook.getSheet("Titres"));
            List<AlbumFormDTO> albums = readAlbumsSheet(workbook.getSheet("Albums"));

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
            artiste.setNomArtiste(row.getCell(getPositionOfKey(ARTISTE_COLUMNS, "NOM")).getStringCellValue());
            artiste.setPrenom(row.getCell(getPositionOfKey(ARTISTE_COLUMNS, "PRENOM")).getStringCellValue());
            artiste.setImage(row.getCell(getPositionOfKey(ARTISTE_COLUMNS, "IMAGE")).getStringCellValue());

            try {
                if (row.getCell(getPositionOfKey(ARTISTE_COLUMNS, "DATE_NAISSANCE")) != null) {
                    String dateNaissanceStr = row.getCell(getPositionOfKey(ARTISTE_COLUMNS, "DATE_NAISSANCE")).getStringCellValue().trim();
                    if (!dateNaissanceStr.isEmpty()) {
                        Date dateNaissance = dateFormat.parse(dateNaissanceStr);
                        artiste.setDateNaissance(dateNaissance);
                    }
                }
                if (row.getCell(getPositionOfKey(ARTISTE_COLUMNS, "DATE_DECES")) != null) {
                    String dateDecesStr = row.getCell(getPositionOfKey(ARTISTE_COLUMNS, "DATE_DECES")).getStringCellValue().trim();
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
            titre.setNom(row.getCell(getPositionOfKey(TITRE_COLUMNS, "NOM")).getStringCellValue());

            String dureeStr = row.getCell(getPositionOfKey(TITRE_COLUMNS, "DUREE")).getStringCellValue();
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
            String[] artistesIdsStr = row.getCell(getPositionOfKey(TITRE_COLUMNS, "ARTISTE_IDS")).getStringCellValue().split(",");
            List<Long> artistesIds = new ArrayList<>();
            for (String idStr : artistesIdsStr) {
                if (StringUtils.isNotBlank(idStr)) {
                    artistesIds.add(Long.parseLong(idStr.trim()));
                }
            }
            titre.setArtistesIds(artistesIds);

            titres.add(titre);
        }
        return titres;
    }

    private List<AlbumFormDTO> readAlbumsSheet(Sheet sheet) throws IOException {
        List<AlbumFormDTO> albums = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            AlbumFormDTO album = new AlbumFormDTO();
            album.setId(Long.parseLong(row.getCell(getPositionOfKey(ALBUM_COLUMNS, "ID")).getStringCellValue()));
            album.setNom(row.getCell(getPositionOfKey(ALBUM_COLUMNS, "NOM")).getStringCellValue());

            // Extraction des IDs des artistes
            String[] artistesIdsStr = row.getCell(getPositionOfKey(ALBUM_COLUMNS, "ARTISTE_IDS")).getStringCellValue().split(",");
            List<Long> artistesIds = new ArrayList<>();
            for (String idStr : artistesIdsStr) {
                if (StringUtils.isNotBlank(idStr)) {
                    artistesIds.add(Long.parseLong(idStr.trim()));
                }
            }
            album.setArtistesIds(artistesIds);

            // Extraction des IDs des titres
            String titresIdsStr = row.getCell(getPositionOfKey(ALBUM_COLUMNS, "TITRE_IDS")).getStringCellValue();
            List<Long> titresIds = Arrays.stream(titresIdsStr.split(","))
                .filter(StringUtils::isNotBlank)
                .map(Long::parseLong)
                .collect(Collectors.toList());
            List<TitreDTO> titres = titreService
                .getTitres()
                .stream()
                .filter(t -> titresIds.contains(t.getId()))
                .collect(Collectors.toList());
            album.setTitres(titres);

            album.setTaille(row.getCell(getPositionOfKey(ALBUM_COLUMNS, "TAILLE")).getStringCellValue());
            album.setStatus(row.getCell(getPositionOfKey(ALBUM_COLUMNS, "STATUS")).getStringCellValue());
            album.setImage(row.getCell(getPositionOfKey(ALBUM_COLUMNS, "IMAGE")).getStringCellValue());

            albums.add(album);
        }
        return albums;
    }

    private void createSheet(Workbook workbook, String sheetName, List<Map<String, String>> data, Map<String, String> columnsMap) {
        Sheet sheet = workbook.createSheet(sheetName);
        if (data.isEmpty()) return;

        // Header row
        Row headerRow = sheet.createRow(0);
        int headerIndex = 0;
        for (String columnTitle : columnsMap.values()) {
            headerRow.createCell(headerIndex++).setCellValue(columnTitle);
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
