package be.enkidu.vinyles.business.service.dto;

import java.util.List;

public class AlbumFormDTO {

    private Long id;
    private String image;
    private String nom;
    private List<Long> artistesIds;
    private List<Long> titresIds;
    private String taille;
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<Long> getArtistesIds() {
        return artistesIds;
    }

    public void setArtistesIds(List<Long> artistesIds) {
        this.artistesIds = artistesIds;
    }

    public List<Long> getTitresIds() {
        return titresIds;
    }

    public void setTitresIds(List<Long> titresIds) {
        this.titresIds = titresIds;
    }

    public String getTaille() {
        return taille;
    }

    public void setTaille(String taille) {
        this.taille = taille;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
