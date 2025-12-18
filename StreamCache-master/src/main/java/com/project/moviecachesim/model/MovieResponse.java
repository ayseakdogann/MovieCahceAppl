package com.project.moviecachesim.model;

public class MovieResponse {
    private Movie movie;
    private String message;
    private boolean cacheHit;
    private long duration;

    public MovieResponse(Movie movie, String message, boolean cacheHit, long duration) {
        this.movie = movie;
        this.message = message;
        this.cacheHit = cacheHit;
        this.duration = duration;
    }

    public Movie getMovie() { return movie; }
    public String getMessage() { return message; }
    public boolean isCacheHit() { return cacheHit; }
    public long getDuration() { return duration; }

}
