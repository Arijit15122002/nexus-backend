package com.arijit.nexus_backend.ai.response.parser.service;

import com.arijit.nexus_backend.ai.response.dto.ResponseSection;
import com.arijit.nexus_backend.ai.response.parser.domain.CodeResponseParser;
import com.arijit.nexus_backend.ai.response.parser.domain.LearningResponseParser;
import com.arijit.nexus_backend.ai.tool.entity.ToolDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParserRoutingService {

    private final LearningResponseParser
            learningResponseParser;

    private final CodeResponseParser
            codeResponseParser;

    private final ResponseParserService responseParserService;

    public List<ResponseSection> parse(

            ToolDomain domain,

            String rawResponse

    ) {

        return switch (domain) {

            case LEARNING ->

                    learningResponseParser
                            .parse(rawResponse);

            case CODE ->

                    codeResponseParser
                            .parse(rawResponse);

            default ->

                    responseParserService
                            .parseSections(rawResponse);

        };

    }

}