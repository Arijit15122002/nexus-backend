package com.arijit.nexus_backend.ai.response.parser.domain;

import com.arijit.nexus_backend.ai.response.dto.ResponseSection;

import java.util.List;

public interface DomainResponseParser {

    List<ResponseSection> parse(
            String rawResponse
    );

}