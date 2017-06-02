package com.example.schmidegv.booklistinguda7;

/**
 * Created by schmidegv on 2017. 06. 01..
 */

public class Book {
    private String mName;
    private StringBuilder mAuthor;
    private String mUrl;
    private String mDescription;
    private String mImageUrl;

    public Book(String name, StringBuilder author, String url, String description, String imageUrl) {
        mName = name;
        mAuthor = author;
        mUrl = url;
        mDescription = description;
        mImageUrl = imageUrl;
    }

    public String getName() {
        return mName;
    }

    public StringBuilder getAuthor() {
        return mAuthor;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getImageUrl() {
        return mImageUrl;
    }
}
