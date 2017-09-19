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
 * Created by jinwoomin on 8/3/16.
 * Converted from http://www.jsonschema2pojo.org/
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder("id", "name", "type", "path")
class Engine {

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
     * @return The name
     */
    /**
     * @param name The name
     */
    @JsonProperty("name")
    @get:JsonProperty("name")
    @set:JsonProperty("name")
    var name: String? = null
    /**
     * @return The type
     */
    /**
     * @param type The type
     */
    @JsonProperty("type")
    @get:JsonProperty("type")
    @set:JsonProperty("type")
    var type: String? = null
    /**
     * @return The path
     */
    /**
     * @param path The path
     */
    @JsonProperty("path")
    @get:JsonProperty("path")
    @set:JsonProperty("path")
    var path: String? = null
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

    override fun toString(): String? {
        return this.name
    }
}