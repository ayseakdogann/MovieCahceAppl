package com.project.moviecachesim.model;

import jakarta.persistence.*;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    private double duration;

    private String imageUrl; // Resim Linki

    private String genre;

    private int accessCount = 0; // İzlenme Sayısı

    // --- CONSTRUCTORLAR ---
    public Movie() {
    }

    public Movie(String title, String description, double duration, String imageUrl,String genre) {
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.imageUrl = imageUrl;
        this.genre=genre;
        this.accessCount = 0;
    }

    // --- GETTER VE SETTERLAR ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }


    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getDuration() { return duration; }
    public void setDuration(double duration) { this.duration = duration; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getAccessCount() { return accessCount; }
    public void setAccessCount(int accessCount) { this.accessCount = accessCount; }
}