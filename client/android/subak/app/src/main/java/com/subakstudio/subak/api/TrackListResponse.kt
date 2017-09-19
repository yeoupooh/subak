package com.subakstudio.subak.api

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

import java.util.ArrayList
import java.util.HashMap

import javax.annotation.Generated

/**
 * Created by jinwoomin on 8/6/16.
 * Converted from http://www.jsonschema2pojo.org/
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder("found", "tracks")
class TrackListResponse {

    /**
     * @return The found
     */
    /**
     * @param found The found
     */
    @JsonProperty("found")
    @get:JsonProperty("found")
    @set:JsonProperty("found")
    var found: Int? = null
    /**
     * @return The tracks
     */
    /**
     * @param tracks The tracks
     */
    @JsonProperty("tracks")
    @get:JsonProperty("tracks")
    @set:JsonProperty("tracks")
    var tracks: List<Track> = ArrayList()
    @JsonIgnore
    private val additionalProperties = HashMap<String, Any>()

    @JsonAnyGetter
    fun getAdditionalProperties(): Map<String, Any> {
        return this.additionalProperties
    }

    @JsonAnySetter
    fun setAdditionalProperty(name: String, value: Any) {
        this.additionalProperties.put(name, value)
    }

}
