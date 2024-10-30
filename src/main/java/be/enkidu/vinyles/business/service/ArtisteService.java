package be.enkidu.vinyles.business.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ArtisteService {

    public List<Map<String, String>> exportArtistes() {
        List<Map<String, String>> artistes = new ArrayList<>();

        // Exemple de données d'artistes pour export
        Map<String, String> artiste1 = new HashMap<>();
        artiste1.put("ID", "1");
        artiste1.put("Nom", "Doe");
        artiste1.put("Prénom", "John");
        artiste1.put("Date de Naissance", "1980-01-01");
        artiste1.put("Date de Décès", "");

        Map<String, String> artiste2 = new HashMap<>();
        artiste2.put("ID", "2");
        artiste2.put("Nom", "Smith");
        artiste2.put("Prénom", "Jane");
        artiste2.put("Date de Naissance", "1985-05-15");
        artiste2.put("Date de Décès", "");

        artistes.add(artiste1);
        artistes.add(artiste2);

        return artistes;
    }
}
