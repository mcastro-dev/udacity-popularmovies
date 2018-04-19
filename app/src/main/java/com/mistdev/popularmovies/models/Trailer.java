package com.mistdev.popularmovies.models;

/**
 * Created by kastr on 4/08/2016.
 *
 */
public class Trailer {

    public String name;
    public String videoUrl;
    public String thumbnailUrl;

    public Trailer(String name, String videoUrl, String thumbnailUrl) {
        this.name = name;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

}
