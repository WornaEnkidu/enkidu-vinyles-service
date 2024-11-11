package be.enkidu.vinyles.business.service;

import static be.enkidu.vinyles.business.service.constant.ExcelColumnConstants.*;

import be.enkidu.vinyles.business.service.dto.AlbumDTO;
import be.enkidu.vinyles.business.service.dto.ArtisteDTO;
import be.enkidu.vinyles.business.service.dto.TitreDTO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class TemporaryDataStoreService {

    public static final String FILE_PATH = "./exported_data/vinyles_export.xlsx";

    public TemporaryDataStoreService() {
        try {
            initializeFileIfNotExists();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeFileIfNotExists() throws IOException {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            // Créer les répertoires s'ils n'existent pas
            Files.createDirectories(Paths.get("./exported_data"));

            // Créer un nouveau fichier Excel avec des feuilles par défaut
            try (Workbook workbook = new XSSFWorkbook()) {
                workbook.createSheet("Artistes");
                workbook.createSheet("Titres");
                workbook.createSheet("Albums");

                // Sauvegarder le fichier vide
                try (FileOutputStream fileOut = new FileOutputStream(FILE_PATH)) {
                    workbook.write(fileOut);
                }

                System.out.println("Le fichier Excel a été créé : " + FILE_PATH);
            }
        } else {
            System.out.println("Le fichier Excel existe déjà : " + FILE_PATH);
        }
    }

    public void saveData(List<ArtisteDTO> artistes, List<TitreDTO> titres, List<AlbumDTO> albums) {
        Workbook workbook = new XSSFWorkbook();

        // Créer les feuilles pour Artistes, Titres et Albums
        createArtistesSheet(workbook, artistes);
        createTitresSheet(workbook, titres);
        createAlbumsSheet(workbook, albums);

        try {
            Files.createDirectories(Paths.get("./exported_data"));
            FileOutputStream fileOut = new FileOutputStream(FILE_PATH);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArtisteDTO saveArtiste(ArtisteDTO newArtiste) throws IOException {
        try (FileInputStream fis = new FileInputStream(FILE_PATH); Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheet("Artistes");
            boolean updated = false;

            // Recherche l'artiste par ID et met à jour ses informations s'il existe
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (
                    row != null &&
                    newArtiste.getId() != null &&
                    ((long) row.getCell(getPositionOfKey(ARTISTE_COLUMNS, "ID")).getNumericCellValue()) == newArtiste.getId()
                ) {
                    updateArtisteRow(row, newArtiste);
                    updated = true;
                    break;
                }
            }

            // Si l'artiste n'existe pas, ajoute une nouvelle ligne
            if (!updated) {
                newArtiste.setId(generateNewId(sheet, ARTISTE_COLUMNS));

                Row newRow = sheet.createRow(sheet.getLastRowNum() + 1);
                updateArtisteRow(newRow, newArtiste);
            }

            saveWorkbook(workbook);

            return newArtiste;
        } catch (IOException e) {
            throw e;
        }
    }

    public List<ArtisteDTO> getArtistes() throws IOException {
        List<ArtisteDTO> artistes = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try (FileInputStream fis = new FileInputStream(FILE_PATH); Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheet("Artistes");
            if (sheet == null) {
                return artistes; // Retourne une liste vide si la feuille n'existe pas
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Commence à 1 pour ignorer l'en-tête
                Row row = sheet.getRow(i);
                if (row != null) {
                    ArtisteDTO artiste = new ArtisteDTO();
                    artiste.setId((long) row.getCell(getPositionOfKey(ARTISTE_COLUMNS, "ID")).getNumericCellValue());
                    artiste.setNomArtiste(row.getCell(getPositionOfKey(ARTISTE_COLUMNS, "NOM")).getStringCellValue());
                    artiste.setPrenom(row.getCell(getPositionOfKey(ARTISTE_COLUMNS, "PRENOM")).getStringCellValue());
                    artiste.setImage(row.getCell(getPositionOfKey(ARTISTE_COLUMNS, "IMAGE")).getStringCellValue());

                    try {
                        // Gère Date Naissance en tant que Date
                        if (row.getCell(getPositionOfKey(ARTISTE_COLUMNS, "DATE_NAISSANCE")) != null) {
                            String dateNaissanceStr = row
                                .getCell(getPositionOfKey(ARTISTE_COLUMNS, "DATE_NAISSANCE"))
                                .getStringCellValue()
                                .trim();
                            if (!dateNaissanceStr.isEmpty()) {
                                Date dateNaissance = dateFormat.parse(dateNaissanceStr);
                                artiste.setDateNaissance(dateNaissance);
                            }
                        }

                        // Gère Date Décès en tant que Date
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
            }
        }

        return artistes;
    }

    public TitreDTO saveTitre(TitreDTO newTitre) throws IOException {
        try (FileInputStream fis = new FileInputStream(FILE_PATH); Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheet("Titres");
            boolean updated = false;

            // Recherche le titre par ID et met à jour ses informations s'il existe
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (
                    row != null &&
                    newTitre.getId() != null &&
                    ((long) row.getCell(getPositionOfKey(TITRE_COLUMNS, "ID")).getNumericCellValue()) == newTitre.getId()
                ) {
                    updateTitreRow(row, newTitre);
                    updated = true;
                    break;
                }
            }

            // Si le titre n'existe pas, ajoute une nouvelle ligne
            if (!updated) {
                newTitre.setId(generateNewId(sheet, TITRE_COLUMNS));

                Row newRow = sheet.createRow(sheet.getLastRowNum() + 1);
                updateTitreRow(newRow, newTitre);
            }

            saveWorkbook(workbook);

            return newTitre;
        } catch (IOException e) {
            throw e;
        }
    }

    public List<TitreDTO> getTitres() throws IOException {
        List<TitreDTO> titres = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(FILE_PATH); Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheet("Titres");
            if (sheet == null) {
                return titres; // Retourne une liste vide si la feuille n'existe pas
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Commence à 1 pour ignorer l'en-tête
                Row row = sheet.getRow(i);
                if (row != null) {
                    TitreDTO titre = new TitreDTO();
                    titre.setId((long) row.getCell(getPositionOfKey(TITRE_COLUMNS, "ID")).getNumericCellValue());
                    titre.setNom(row.getCell(getPositionOfKey(TITRE_COLUMNS, "NOM")).getStringCellValue());
                    titre.setDuree((int) row.getCell(getPositionOfKey(TITRE_COLUMNS, "DUREE")).getNumericCellValue());

                    // Convertit les IDs des artistes en liste
                    String artistesIdsStr = row.getCell(getPositionOfKey(TITRE_COLUMNS, "ARTISTE_IDS")).getStringCellValue();
                    List<Long> artistesIds = new ArrayList<>();
                    for (String id : artistesIdsStr.split(",")) {
                        if (StringUtils.isNotBlank(id)) {
                            artistesIds.add(Long.parseLong(id.trim()));
                        }
                    }
                    titre.setArtistesIds(artistesIds);

                    titres.add(titre);
                }
            }
        }

        return titres;
    }

    public AlbumDTO saveAlbum(AlbumDTO newAlbum) throws IOException {
        try (FileInputStream fis = new FileInputStream(FILE_PATH); Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheet("Albums");
            boolean updated = false;

            // Recherche l'album par ID et met à jour ses informations s'il existe
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (
                    row != null &&
                    newAlbum.getId() != null &&
                    ((long) row.getCell(getPositionOfKey(ALBUM_COLUMNS, "ID")).getNumericCellValue()) == newAlbum.getId()
                ) {
                    updateAlbumRow(row, newAlbum);
                    updated = true;
                    break;
                }
            }

            // Si l'album n'existe pas, ajoute une nouvelle ligne
            if (!updated) {
                newAlbum.setId(generateNewId(sheet, ALBUM_COLUMNS));

                Row newRow = sheet.createRow(sheet.getLastRowNum() + 1);
                updateAlbumRow(newRow, newAlbum);
            }

            saveWorkbook(workbook);

            return newAlbum;
        } catch (IOException e) {
            throw e;
        }
    }

    public List<AlbumDTO> getAlbums() throws IOException {
        List<AlbumDTO> albums = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(FILE_PATH); Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheet("Albums");
            if (sheet == null) {
                return albums; // Retourne une liste vide si la feuille n'existe pas
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Commence à 1 pour ignorer l'en-tête
                Row row = sheet.getRow(i);
                if (row != null) {
                    AlbumDTO album = new AlbumDTO();
                    album.setId((long) row.getCell(getPositionOfKey(ALBUM_COLUMNS, "ID")).getNumericCellValue());
                    album.setNom(row.getCell(getPositionOfKey(ALBUM_COLUMNS, "NOM")).getStringCellValue());

                    // Convertit les IDs des artistes en liste
                    String artistesIdsStr = row.getCell(getPositionOfKey(ALBUM_COLUMNS, "ARTISTE_IDS")).getStringCellValue();
                    List<Long> artistesIds = new ArrayList<>();
                    for (String id : artistesIdsStr.split(",")) {
                        if (StringUtils.isNotBlank(id)) {
                            artistesIds.add(Long.parseLong(id.trim()));
                        }
                    }
                    album.setArtistesIds(artistesIds);

                    // Convertit les IDs des titres en liste
                    String titresIdsStr = row.getCell(getPositionOfKey(ALBUM_COLUMNS, "TITRE_IDS")).getStringCellValue();
                    List<Long> titresIds = new ArrayList<>();
                    for (String id : titresIdsStr.split(",")) {
                        if (StringUtils.isNotBlank(id)) {
                            titresIds.add(Long.parseLong(id.trim()));
                        }
                    }
                    album.setTitresIds(titresIds);

                    album.setTaille(row.getCell(getPositionOfKey(ALBUM_COLUMNS, "TAILLE")).getStringCellValue());
                    album.setStatus(row.getCell(getPositionOfKey(ALBUM_COLUMNS, "STATUS")).getStringCellValue());
                    album.setImage(row.getCell(getPositionOfKey(ALBUM_COLUMNS, "IMAGE")).getStringCellValue());
                    albums.add(album);
                }
            }
        }

        return albums;
    }

    private long generateNewId(Sheet sheet, Map<String, String> columns) {
        long maxId = 0;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                long currentId = (long) row.getCell(getPositionOfKey(columns, "ID")).getNumericCellValue();
                if (currentId > maxId) {
                    maxId = currentId;
                }
            }
        }
        return maxId + 1;
    }

    private void saveWorkbook(Workbook workbook) {
        try {
            Files.createDirectories(Paths.get("./exported_data"));
            FileOutputStream fileOut = new FileOutputStream(FILE_PATH);
            workbook.write(fileOut);
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateArtisteRow(Row row, ArtisteDTO artiste) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        row.createCell(getPositionOfKey(ARTISTE_COLUMNS, "ID")).setCellValue(artiste.getId());
        row.createCell(getPositionOfKey(ARTISTE_COLUMNS, "NOM")).setCellValue(artiste.getNomArtiste());
        row.createCell(getPositionOfKey(ARTISTE_COLUMNS, "PRENOM")).setCellValue(artiste.getPrenom());
        row.createCell(getPositionOfKey(ARTISTE_COLUMNS, "IMAGE")).setCellValue(artiste.getImage());
        row
            .createCell(getPositionOfKey(ARTISTE_COLUMNS, "DATE_NAISSANCE"))
            .setCellValue(artiste.getDateNaissance() != null ? dateFormat.format(artiste.getDateNaissance()) : "");
        row
            .createCell(getPositionOfKey(ARTISTE_COLUMNS, "DATE_DECES"))
            .setCellValue(artiste.getDateDeces() != null ? dateFormat.format(artiste.getDateDeces()) : "");
    }

    private void updateTitreRow(Row row, TitreDTO titre) {
        row.createCell(getPositionOfKey(TITRE_COLUMNS, "ID")).setCellValue(titre.getId());
        row.createCell(getPositionOfKey(TITRE_COLUMNS, "NOM")).setCellValue(titre.getNom());

        Integer duree = titre.getDuree();
        row.createCell(getPositionOfKey(TITRE_COLUMNS, "DUREE")).setCellValue(duree != null ? duree : 0);

        // Concatène les IDs des artistes en chaîne de caractères
        String artistesIds = "";
        if (titre.getArtistesIds() != null) {
            artistesIds = String.join(",", titre.getArtistesIds().stream().map(String::valueOf).toList());
        }
        row.createCell(getPositionOfKey(TITRE_COLUMNS, "ARTISTE_IDS")).setCellValue(artistesIds);
    }

    private void updateAlbumRow(Row row, AlbumDTO album) {
        row.createCell(getPositionOfKey(ALBUM_COLUMNS, "ID")).setCellValue(album.getId());
        row.createCell(getPositionOfKey(ALBUM_COLUMNS, "NOM")).setCellValue(album.getNom());

        // Concatène les IDs des artistes en chaîne de caractères
        String artistesIds = "";
        if (album.getArtistesIds() != null) {
            artistesIds = String.join(",", album.getArtistesIds().stream().map(String::valueOf).toList());
        }
        row.createCell(getPositionOfKey(ALBUM_COLUMNS, "ARTISTE_IDS")).setCellValue(artistesIds);

        // Concatène les IDs des titres en chaîne de caractères
        String titresIds = "";
        if (album.getTitresIds() != null) {
            titresIds = String.join(",", album.getTitresIds().stream().map(String::valueOf).toList());
        }
        row.createCell(getPositionOfKey(ALBUM_COLUMNS, "TITRE_IDS")).setCellValue(titresIds);

        row.createCell(getPositionOfKey(ALBUM_COLUMNS, "TAILLE")).setCellValue(album.getTaille());
        row.createCell(getPositionOfKey(ALBUM_COLUMNS, "STATUS")).setCellValue(album.getStatus());
        row.createCell(getPositionOfKey(ALBUM_COLUMNS, "IMAGE")).setCellValue(album.getImage());
    }

    private void createArtistesSheet(Workbook workbook, List<ArtisteDTO> artistes) {
        Sheet sheet = workbook.createSheet("Artistes");
        Row header = sheet.createRow(0);
        ARTISTE_COLUMNS.forEach((code, label) -> header.createCell(getPositionOfKey(ARTISTE_COLUMNS, code)).setCellValue(label));

        int rowNum = 1;
        for (ArtisteDTO artiste : artistes) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(getPositionOfKey(ARTISTE_COLUMNS, "ID")).setCellValue(artiste.getId());
            row.createCell(getPositionOfKey(ARTISTE_COLUMNS, "NOM")).setCellValue(artiste.getNomArtiste());
            row.createCell(getPositionOfKey(ARTISTE_COLUMNS, "PRENOM")).setCellValue(artiste.getPrenom());
            row.createCell(getPositionOfKey(ARTISTE_COLUMNS, "IMAGE")).setCellValue(artiste.getImage());
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
            row.createCell(getPositionOfKey(ALBUM_COLUMNS, "IMAGE")).setCellValue(album.getImage());
        }
    }
}
