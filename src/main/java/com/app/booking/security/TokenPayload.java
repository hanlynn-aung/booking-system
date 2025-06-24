package com.app.booking.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.*;

public class TokenPayload {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonDeserialize(using = GrantedAuthorityDeserializer.class)
    @JsonProperty("authorities")
    private Collection<? extends GrantedAuthority> authorities;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }
}

class GrantedAuthorityDeserializer extends JsonDeserializer<Collection<? extends GrantedAuthority>> {

    @Override
    public Collection<? extends GrantedAuthority> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        if (!node.isArray()) {
            return null;
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        Iterator<JsonNode> elements = node.elements();
        while (elements.hasNext()) {
            JsonNode element = elements.next();
            authorities.add(new SimpleGrantedAuthority(element.get("authority").asText()));
        }

        return authorities;
    }
}
