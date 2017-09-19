package com.subakstudio.subak.api

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

import java.util.HashMap

import javax.annotation.Generated

/**
 * Created by jinwoomin on 8/6/16.
 * Converted from http://www.jsonschema2pojo.org/
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder("id", "artist", "track", "length", "file", "link", "size", "bitrate", "albumart")
class Track {

    /**
     * @return The id
     */
    /**
     * @param id The id
     */
    @JsonProperty("id")
    @get:JsonProperty("id")
    @set:JsonProperty("id")
    var id: String? = null
    /**
     * @return The artist
     */
    /**
     * @param artist The artist
     */
    @JsonProperty("artist")
    @get:JsonProperty("artist")
    @set:JsonProperty("artist")
    var artist: String? = null
    /**
     * @return The track
     */
    /**
     * @param track The track
     */
    @JsonProperty("track")
    @get:JsonProperty("track")
    @set:JsonProperty("track")
    var track: String? = null
    /**
     * @return The length
     */
    /**
     * @param length The length
     */
    @JsonProperty("length")
    @get:JsonProperty("length")
    @set:JsonProperty("length")
    var length: Int? = null
    /**
     * @return The file
     */
    /**
     * @param file The file
     */
    @JsonProperty("file")
    @get:JsonProperty("file")
    @set:JsonProperty("file")
    var file: String? = null
    /**
     * @return The link
     */
    /**
     * @param link The link
     */
    @JsonProperty("link")
    @get:JsonProperty("link")
    @set:JsonProperty("link")
    var link: String? = null
    /**
     * @return The size
     */
    /**
     * @param size The size
     */
    @JsonProperty("size")
    @get:JsonProperty("size")
    @set:JsonProperty("size")
    var size: Int? = null
    /**
     * @return The bitrate
     */
    /**
     * @param bitrate The bitrate
     */
    @JsonProperty("bitrate")
    @get:JsonProperty("bitrate")
    @set:JsonProperty("bitrate")
    var bitrate: String? = null
    /**
     * @return The albumart
     */
    /**
     * @param albumart The albumart
     */
    @JsonProperty("albumart")
    @get:JsonProperty("albumart")
    @set:JsonProperty("albumart")
    var albumart: String? = null
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