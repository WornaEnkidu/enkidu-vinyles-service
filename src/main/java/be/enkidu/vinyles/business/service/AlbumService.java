package be.enkidu.vinyles.business.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AlbumService {

    public List<Map<String, String>> exportAlbums() {
        List<Map<String, String>> albums = new ArrayList<>();

        // Exemple de données d'albums pour export
        Map<String, String> album1 = new HashMap<>();
        album1.put("ID", "1");
        album1.put("Nom", "Album 1");
        album1.put("Artiste", "John Doe");
        album1.put("Taille", "33t");
        album1.put("Status", "Disponible");

        Map<String, String> album2 = new HashMap<>();
        album2.put("ID", "2");
        album2.put("Nom", "Album 2");
        album2.put("Artiste", "Jane Smith");
        album2.put("Taille", "45t");
        album2.put("Status", "Indisponible");

        albums.add(album1);
        albums.add(album2);

        return albums;
    }
}
