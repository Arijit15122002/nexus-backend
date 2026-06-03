package com.arijit.nexus_backend.ai.response.service;

import com.arijit.nexus_backend.ai.response.dto.AIResponse;
import com.arijit.nexus_backend.ai.response.dto.ResponseSection;
import com.arijit.nexus_backend.ai.response.factory.CodeResponseFactory;
import com.arijit.nexus_backend.ai.response.parser.service.ParserRoutingService;
import com.arijit.nexus_backend.ai.tool.entity.ToolCapability;
import com.arijit.nexus_backend.ai.tool.entity.ToolDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResponseGenerationService {

    private final ParserRoutingService
            parserRoutingService;

    private final CodeResponseFactory codeResponseFactory;

    public AIResponse generateResponse(

            ToolCapability capability,

            ToolDomain domain,

            String rawResponse

    ) {

        // =========================
        // PARSE RESPONSE
        // =========================

        List<ResponseSection> sections =
                parserRoutingService.parse(

                        domain,

                        rawResponse

                );

        // =========================
        // BUILD AI RESPONSE
        // =========================

        if (domain == ToolDomain.CODE) {

            return codeResponseFactory.build(

                    capability,

                    rawResponse,

                    sections

            );

        }

        return AIResponse.builder()

                .responseId(
                        UUID.randomUUID().toString()
                )

                .capability(
                        capability
                )

                .domain(
                        domain
                )

                .rawResponse(
                        rawResponse
                )

                .title(
                        generateTitle(
                                capability
                        )
                )

                .summary(
                        generateSummary(
                                rawResponse
                        )
                )

                .sections(
                        sections
                )

                .metadata(
                        buildMetadata(
                                sections,
                                domain
                        )
                )

                .createdAt(
                        LocalDateTime.now()
                )

                .build();

    }

    // =========================
    // TITLE
    // =========================

    private String generateTitle(
            ToolCapability capability
    ) {

        return capability.name()
                .replace("_", " ");

    }

    // =========================
    // SUMMARY
    // =========================

    private String generateSummary(
            String rawResponse
    ) {

        if (
                rawResponse == null
                        || rawResponse.isBlank()
        ) {

            return "";

        }

        return rawResponse.length() > 250

                ? rawResponse.substring(
                0,
                250
        ) + "..."

                : rawResponse;

    }

    // =========================
    // METADATA
    // =========================

    private Map<String, Object> buildMetadata(

            List<ResponseSection> sections,

            ToolDomain domain

    ) {

        return Map.of(

                "totalSections",
                sections.size(),

                "domain",
                domain.name(),

                "generatedAt",
                LocalDateTime.now().toString(),

                "renderMode",
                determineRenderMode(
                        domain
                )

        );

    }

    // =========================
    // RENDER MODE
    // =========================

    private String determineRenderMode(
            ToolDomain domain
    ) {

        return switch (domain) {

            case CODE -> "CODE_VIEW";

            case LEARNING -> "ROADMAP_VIEW";

            case PRODUCTIVITY -> "TASK_VIEW";

            case AI -> "ARCHITECTURE_VIEW";

            default -> "STANDARD_VIEW";

        };

    }

}