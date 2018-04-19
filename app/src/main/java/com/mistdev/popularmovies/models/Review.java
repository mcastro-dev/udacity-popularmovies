package com.mistdev.popularmovies.models;

/**
 * Created by kastr on 5/08/2016.
 *
 */
public class Review {

    public String id;
    public String author;
    public String content;
    public String url;

    public Review(String id, String author, String content, String url) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.url = url;
    }

}
