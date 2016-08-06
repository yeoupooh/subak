package com.subakstudio.subak.api;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

/**
 * Created by jinwoomin on 8/6/16.
 * Converted from http://www.jsonschema2pojo.org/
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "id",
        "artist",
        "track",
        "length",
        "file",
        "link",
        "size",
        "bitrate",
        "albumart"
})
public class Track {

    @JsonProperty("id")
    private String id;
    @JsonProperty("artist")
    private String artist;
    @JsonProperty("track")
    private String track;
    @JsonProperty("length")
    private Integer length;
    @JsonProperty("file")
    private String file;
    @JsonProperty("link")
    private String link;
    @JsonProperty("size")
    private Integer size;
    @JsonProperty("bitrate")
    private String bitrate;
    @JsonProperty("albumart")
    private String albumart;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The id
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The artist
     */
    @JsonProperty("artist")
    public String getArtist() {
        return artist;
    }

    /**
     * @param artist The artist
     */
    @JsonProperty("artist")
    public void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * @return The track
     */
    @JsonProperty("track")
    public String getTrack() {
        return track;
    }

    /**
     * @param track The track
     */
    @JsonProperty("track")
    public void setTrack(String track) {
        this.track = track;
    }

    /**
     * @return The length
     */
    @JsonProperty("length")
    public Integer getLength() {
        return length;
    }

    /**
     * @param length The length
     */
    @JsonProperty("length")
    public void setLength(Integer length) {
        this.length = length;
    }

    /**
     * @return The file
     */
    @JsonProperty("file")
    public String getFile() {
        return file;
    }

    /**
     * @param file The file
     */
    @JsonProperty("file")
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * @return The link
     */
    @JsonProperty("link")
    public String getLink() {
        return link;
    }

    /**
     * @param link The link
     */
    @JsonProperty("link")
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * @return The size
     */
    @JsonProperty("size")
    public Integer getSize() {
        return size;
    }

    /**
     * @param size The size
     */
    @JsonProperty("size")
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * @return The bitrate
     */
    @JsonProperty("bitrate")
    public String getBitrate() {
        return bitrate;
    }

    /**
     * @param bitrate The bitrate
     */
    @JsonProperty("bitrate")
    public void setBitrate(String bitrate) {
        this.bitrate = bitrate;
    }

    /**
     * @return The albumart
     */
    @JsonProperty("albumart")
    public String getAlbumart() {
        return albumart;
    }

    /**
     * @param albumart The albumart
     */
    @JsonProperty("albumart")
    public void setAlbumart(String albumart) {
        this.albumart = albumart;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}