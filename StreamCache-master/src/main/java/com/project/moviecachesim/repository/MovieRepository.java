package com.project.moviecachesim.repository;

import com.project.moviecachesim.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    // YENİ EKLENEN SİHİRLİ METOD:
    // SQL: SELECT * FROM movies WHERE LOWER(title) = LOWER(girilenDeger)
    Optional<Movie> findByTitleIgnoreCase(String title);
    // Aynı türdeki en popüler filmi bulmak için (Tahminleme için kullanılacak)
    List<Movie> findByGenreOrderByAccessCountDesc(String genre);
}