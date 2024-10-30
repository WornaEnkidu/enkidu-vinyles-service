package be.enkidu.vinyles.business.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class TitreService {

    public List<Map<String, String>> exportTitres() {
        List<Map<String, String>> titres = new ArrayList<>();

        // Exemple de données de titres pour export
        Map<String, String> titre1 = new HashMap<>();
        titre1.put("ID", "1");
        titre1.put("Nom", "Chanson A");
        titre1.put("Durée", "03:45");
        titre1.put("Artiste", "John Doe");

        Map<String, String> titre2 = new HashMap<>();
        titre2.put("ID", "2");
        titre2.put("Nom", "Chanson B");
        titre2.put("Durée", "04:30");
        titre2.put("Artiste", "Jane Smith");

        titres.add(titre1);
        titres.add(titre2);

        return titres;
    }
}
