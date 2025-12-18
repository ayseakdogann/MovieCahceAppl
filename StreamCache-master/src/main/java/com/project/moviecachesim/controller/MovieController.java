package com.project.moviecachesim.controller;

import com.project.moviecachesim.model.Movie;
import com.project.moviecachesim.model.MovieResponse;
import com.project.moviecachesim.service.MovieCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MovieController {

    @Autowired
    private MovieCacheService service;

    @GetMapping("/movies/{id}")
    public MovieResponse getMovie(@PathVariable Long id) {
        return service.getMovie(id);
    }

    @GetMapping("/movies/list")
    public List<Movie> getAllMovies() {
        return service.getAllMoviesSorted();
    }

    @GetMapping("/cache-content")
    public List<Movie> getCacheContent() {
        return service.getCurrentCache();
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return service.getStats();
    }
}