package be.enkidu.vinyles.business.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ExcelService {

    private final ArtisteService artisteService;
    private final TitreService titreService;
    private final AlbumService albumService;

    public ExcelService(ArtisteService artisteService, TitreService titreService, AlbumService albumService) {
        this.artisteService = artisteService;
        this.titreService = titreService;
        this.albumService = albumService;
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
