package be.enkidu.vinyles.business.web.rest;

import be.enkidu.vinyles.business.service.ExcelService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/excel")
public class ExcelResource {

    private final ExcelService excelService;

    public ExcelResource(ExcelService excelService) {
        this.excelService = excelService;
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportDataToExcel() throws IOException {
        byte[] excelData = excelService.exportDataToExcel();

        // Génération du nom de fichier
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String fileName = "inventaire-vinyles-" + LocalDateTime.now().format(formatter) + ".xlsx";

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(excelData);
    }
}
