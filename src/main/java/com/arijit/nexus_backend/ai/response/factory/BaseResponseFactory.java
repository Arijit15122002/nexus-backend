package com.arijit.nexus_backend.ai.response.factory;

import com.arijit.nexus_backend.ai.response.dto.AIResponse;
import com.arijit.nexus_backend.ai.response.dto.ResponseSection;
import com.arijit.nexus_backend.ai.response.entity.SectionType;
import com.arijit.nexus_backend.ai.tool.entity.ToolCapability;
import com.arijit.nexus_backend.ai.tool.entity.ToolDomain;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class BaseResponseFactory {

    public AIResponse buildBasicResponse(

            ToolCapability capability,

            ToolDomain domain,

            String rawResponse

    ) {

        ResponseSection mainSection =
                ResponseSection.builder()

                        .sectionType(
                                SectionType.TEXT
                        )

                        .title(
                                "AI Response"
                        )

                        .content(
                                rawResponse
                        )

                        .order(1)

                        .importance(10)

                        .build();

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

                .title(
                        capability.name()
                )

                .summary(
                        rawResponse.length() > 150
                                ? rawResponse.substring(0, 150)
                                : rawResponse
                )

                .sections(
                        List.of(mainSection)
                )

                .metadata(
                        Map.of(
                                "generatedBy",
                                "Nexus AI"
                        )
                )

                .createdAt(
                        LocalDateTime.now()
                )

                .build();

    }

}