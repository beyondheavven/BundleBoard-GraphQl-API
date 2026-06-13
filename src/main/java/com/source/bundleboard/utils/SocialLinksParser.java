package com.source.bundleboard.utils;

import com.source.bundleboard.author.dto.SocialLink;
import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocialLinksParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<SocialLink> parseSocialLinks(Json json) {
        if (json == null) {
            return new ArrayList<>();
        }

        String jsonStr = json.asString();

        if (jsonStr.isBlank() || jsonStr.trim().equals("{}")) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(
                    jsonStr,
                    new TypeReference<>() {}
            );
        } catch (Exception e) {
            log.error("🔴 [JSON_PARSE_ERROR]: Failed to decode social links. Data: {}", jsonStr, e);
            return new ArrayList<>();
        }
    }
}
