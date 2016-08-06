package com.subakstudio.subak.api;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

/**
 * Created by jinwoomin on 8/6/16.
 * Converted from http://www.jsonschema2pojo.org/
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "found",
        "tracks"
})
public class TrackListResponse {

    @JsonProperty("found")
    private Integer found;
    @JsonProperty("tracks")
    private List<Track> tracks = new ArrayList<Track>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The found
     */
    @JsonProperty("found")
    public Integer getFound() {
        return found;
    }

    /**
     * @param found The found
     */
    @JsonProperty("found")
    public void setFound(Integer found) {
        this.found = found;
    }

    /**
     * @return The tracks
     */
    @JsonProperty("tracks")
    public List<Track> getTracks() {
        return tracks;
    }

    /**
     * @param tracks The tracks
     */
    @JsonProperty("tracks")
    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
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
