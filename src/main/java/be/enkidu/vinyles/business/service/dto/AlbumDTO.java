package be.enkidu.vinyles.business.service.dto;

import java.util.List;

public class AlbumDTO {

    private Long id;
    private String image;
    private String nom;
    private List<ArtisteDTO> artistes;
    private List<TitreDTO> titres;
    private String taille;
    private String status;
    private Double prix;

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

    public List<ArtisteDTO> getArtistes() {
        return artistes;
    }

    public void setArtistes(List<ArtisteDTO> artistes) {
        this.artistes = artistes;
    }

    public List<TitreDTO> getTitres() {
        return titres;
    }

    public void setTitres(List<TitreDTO> titres) {
        this.titres = titres;
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

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }
}
