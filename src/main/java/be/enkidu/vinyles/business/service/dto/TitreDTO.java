package be.enkidu.vinyles.business.service.dto;

import java.util.List;

public class TitreDTO {

    private Long id;
    private String nom;
    private Integer duree;
    private List<Long> artistesIds;
    private int ordre;

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Integer getDuree() {
        return duree;
    }

    public void setDuree(Integer duree) {
        this.duree = duree;
    }

    public List<Long> getArtistesIds() {
        return artistesIds;
    }

    public void setArtistesIds(List<Long> artistesIds) {
        this.artistesIds = artistesIds;
    }

    public int getOrdre() {
        return ordre;
    }

    public void setOrdre(int ordre) {
        this.ordre = ordre;
    }
}
