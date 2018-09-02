package hu.ait.android.travetalia.data;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by lanle on 5/9/18.
 */

public class Post implements Serializable{

    private String location;
    private String uid;
    private String author;
    private String caption;
    private String imageUrl;
    private String country;
    private long zIndex;

    public long getzIndex() {
        return zIndex;
    }

    public void setzIndex(long zIndex) {
        this.zIndex = zIndex;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    private double lat;
    private double lng;

    public Post(){}

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Post(String uid, String author, String location, String caption) {
        this.uid = uid;
        this.author = author;
        this.location = location;
        this.caption = caption;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public LatLng getLatLong() {
        return new LatLng(lat, lng);
    }
}
