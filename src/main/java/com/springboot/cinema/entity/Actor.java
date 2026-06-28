package com.springboot.cinema.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "actor_id")
    private int id;

    @Column(name = "actor_name")
    private String actorName;

    @ManyToMany(mappedBy = "actorList")
    private List<Movie> movieList;

    public Actor() {
    }

    public Actor(String actorName) {
        this.actorName = actorName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public List<Movie> getMovieList() {
        return movieList;
    }

    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
    }
}
