package com.project.moviecachesim.service; // Hata 1: Eksik paket tanımı eklendi

import com.project.moviecachesim.model.Movie;
import com.project.moviecachesim.model.MovieResponse;
import com.project.moviecachesim.repository.MovieRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class MovieCacheService {

    @Autowired
    private MovieRepository movieRepository;

    private final Map<Long, Movie> cacheMap = new HashMap<>();
    private final PriorityQueue<Movie> minHeap = new PriorityQueue<>(Comparator.comparingInt(Movie::getAccessCount));
    private final int CACHE_CAPACITY = 5;

    private long totalRequests = 0;
    private long cacheHits = 0;
    private long cacheMisses = 0;

    public MovieResponse getMovie(Long movieId) {
        long startTime = System.nanoTime();
        totalRequests++;

        if (cacheMap.containsKey(movieId)) {
            Movie movie = cacheMap.get(movieId);
            CompletableFuture.runAsync(() -> predictAndPreload(movie));
            return handleCacheHit(movie, startTime); // Hata 3: Metot eklendi
        }

        cacheMisses++;
        Optional<Movie> dbMovieOpt = movieRepository.findById(movieId);

        if (dbMovieOpt.isPresent()) {
            Movie movie = dbMovieOpt.get();
            CompletableFuture.runAsync(() -> predictAndPreload(movie));
            return handleDbFetch(dbMovieOpt, startTime); // Hata 4: Metot eklendi
        }

        return new MovieResponse(null, "Film bulunamadı", false, 0);
    }

    private void predictAndPreload(Movie currentMovie) {
        List<Movie> recommendations = movieRepository.findByGenreOrderByAccessCountDesc(currentMovie.getGenre());
        for (Movie recommended : recommendations) {
            if (!cacheMap.containsKey(recommended.getId()) && !recommended.getId().equals(currentMovie.getId())) {
                addToCache(recommended);
                break;
            }
        }
    }

    @Scheduled(fixedRate = 60000)
    public void applyAging() {
        if (!cacheMap.isEmpty()) {
            cacheMap.values().forEach(m -> m.setAccessCount((int) (m.getAccessCount() * 0.9)));
            minHeap.clear();
            minHeap.addAll(cacheMap.values());
        }
    }

    // --- YARDIMCI METOTLAR (Hataları Çözen Kısımlar) ---
    private MovieResponse handleCacheHit(Movie movie, long startTime) {
        cacheHits++;
        movie.setAccessCount(movie.getAccessCount() + 1);
        minHeap.remove(movie);
        minHeap.add(movie);
        movieRepository.save(movie);

        long duration = (System.nanoTime() - startTime) / 1000;
        return new MovieResponse(movie, "CACHE HIT", true, duration);
    }

    private MovieResponse handleDbFetch(Optional<Movie> opt, long startTime) {
        Movie movie = opt.get();
        movie.setAccessCount(movie.getAccessCount() + 1);
        movieRepository.save(movie);

        addToCache(movie); // Hata 2: 'addTCache' yazım hatası 'addToCache' olarak düzeltildi

        long duration = (System.nanoTime() - startTime) / 1000;
        return new MovieResponse(movie, "CACHE MISS", false, duration);
    }

    private void addToCache(Movie movie) {
        if (cacheMap.size() >= CACHE_CAPACITY) {
            Movie leastUsed = minHeap.poll();
            if (leastUsed != null) {
                cacheMap.remove(leastUsed.getId());
            }
        }
        cacheMap.put(movie.getId(), movie);
        minHeap.add(movie);
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRequests", totalRequests);
        stats.put("cacheHits", cacheHits);
        stats.put("cacheMisses", cacheMisses);
        double hitRatio = totalRequests == 0 ? 0 : (double) cacheHits / totalRequests * 100;
        stats.put("hitRatio", String.format("%.1f", hitRatio));
        return stats;
    }

    public List<Movie> getAllMoviesSorted() {
        List<Movie> all = movieRepository.findAll();
        all.sort((m1, m2) -> Integer.compare(m2.getAccessCount(), m1.getAccessCount()));
        return all;
    }

    public List<Movie> getCurrentCache() {
        List<Movie> list = new ArrayList<>(cacheMap.values());
        list.sort((m1, m2) -> Integer.compare(m2.getAccessCount(), m1.getAccessCount()));
        return list;
    }

    @PostConstruct
    public void initDatabase() {
        movieRepository.deleteAll();
        List<Movie> movies = Arrays.asList(
                // BİLİM KURGU
                new Movie("Inception", "Rüya içinde rüya", 148, "https://image.tmdb.org/t/p/w500/ljsZTbVsrQSqZgWeep2B1QiDKuh.jpg", "Bilim Kurgu"),
                new Movie("Matrix", "Simülasyon dünyası", 136, "https://image.tmdb.org/t/p/w500/f89U3ADr1oiB1s9GkdPOEpXUk5H.jpg", "Bilim Kurgu"),
                new Movie("Interstellar", "Uzay yolculuğu", 169, "https://play-lh.googleusercontent.com/MFtl_llKoQcwejT0O-OYcm0sAEXqzi0rScghSCUCPXlV7hQct7CCtlZ9bH2LNOk1l-MiscRaAIYynBOTVTM", "Bilim Kurgu"),

                // AKSİYON
                new Movie("The Dark Knight", "Batman vs Joker", 152, "https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg", "Aksiyon"),
                new Movie("Gladiator", "Roma generali", 155, "https://image.tmdb.org/t/p/w500/ty8TGRuvJLPUmAR1H1nRIsgwvim.jpg", "Aksiyon"),
                new Movie("Avengers", "Süper kahramanlar", 181, "https://image.tmdb.org/t/p/w500/RYMX2wcKCBAr24UyPD7xwmjaTn.jpg", "Aksiyon"),

                // SUÇ
                new Movie("Godfather", "Mafya babası", 175, "https://image.tmdb.org/t/p/w500/3bhkrj58Vtu7enYsRolD1fZdja1.jpg", "Suç"),
                new Movie("Pulp Fiction", "Kült suç filmi", 154, "https://m.media-amazon.com/images/S/pv-target-images/b44bc23c99db6f56a9cb656892524a617fda117921eb0d1a572ac8de74ef10ba.jpg", "Suç"),
                new Movie("Seven", "Yedi günah", 127, "https://image.tmdb.org/t/p/w500/6yoghtyTpznpBik8EngEmJskVUO.jpg", "Suç"),

                // DRAM
                new Movie("Fight Club", "Dövüş kulübü", 139, "https://image.tmdb.org/t/p/w500/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg", "Dram"),
                new Movie("Titanic", "Batan gemi", 195, "https://image.tmdb.org/t/p/w500/9xjZS2rlVxm8SFx8kPC3aIGCOYQ.jpg", "Dram"),
                new Movie("Forrest Gump", "Koş Forrest koş", 142, "https://image.tmdb.org/t/p/w500/arw2vcBveWOVZr6pxd9XTd1TdQa.jpg", "Dram"),

                // KORKU
                new Movie("The Shining", "Cinnet", 146, "https://m.media-amazon.com/images/I/A1cWZr+V3cL._AC_UF1000,1000_QL80_.jpg", "Korku"),
                new Movie("It", "O", 135, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS0Fzj1yljdjPPMqL3yUAtdAZYndRFTFET9rQ&s", "Korku"),
                new Movie("Conjuring", "Korku Seansı", 112, "https://m.media-amazon.com/images/S/pv-target-images/6e4eace67c7c6ba205215fb4d2caed81361b6f2df1633bb8c5c43890f7ac0a3a.jpg", "Korku"),

                // ANIMASYON
                new Movie("Toy Story", "Oyuncak Hikayesi", 81, "https://image.tmdb.org/t/p/w500/uXDfjJbdP4ijW5hWSBrPrlKpxab.jpg", "Animasyon"),
                new Movie("Lion King", "Aslan Kral", 88, "https://upload.wikimedia.org/wikipedia/en/9/9d/Disney_The_Lion_King_2019.jpg", "Animasyon"),
                new Movie("Spider-Verse", "Örümcek-Adam", 117, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTArGCqmoNS0GPeCOcjPeApqzOETD7VbVBSOw&s", "Animasyon"),

                // ROMANTIK
                new Movie("Titanic", "Batan dev gemi", 195, "https://image.tmdb.org/t/p/w500/9xjZS2rlVxm8SFx8kPC3aIGCOYQ.jpg", "Romantik"),
                new Movie("About Time", "Zamanda Aşk", 123, "https://upload.wikimedia.org/wikipedia/tr/8/88/About_Time_Poster.jpg", "Romantik"),
                new Movie("Notebook", "Not Defteri", 123, "https://play-lh.googleusercontent.com/MGZioJEEiRHyC0kNqBBew_WPCbrK9a1ARy89CBgUbV5UcObaNDt5E0yTutuJKrDXwUbtzh-BUxlms9L_KA", "Romantik"),

                // KOMEDI
                new Movie("G.O.R.A.", "Bir uzay filmi", 127, "https://upload.wikimedia.org/wikipedia/tr/thumb/9/9a/Gora_afi%C5%9F.jpg/250px-Gora_afi%C5%9F.jpg", "Komedi"),
                new Movie("The Hangover", "Felekten Bir Gece", 100, "https://m.media-amazon.com/images/M/MV5BNDI2MzBhNzgtOWYyOS00NDM2LWE0OGYtOGQ0M2FjMTI2NTllXkEyXkFqcGc@._V1_FMjpg_UX1000_.jpg", "Komedi"),
                new Movie("Deadpool", "Küfürbaz kahraman", 108, "https://upload.wikimedia.org/wikipedia/en/thumb/2/23/Deadpool_%282016_poster%29.png/250px-Deadpool_%282016_poster%29.png", "Komedi")
        );
        movieRepository.saveAll(movies);
    }
}