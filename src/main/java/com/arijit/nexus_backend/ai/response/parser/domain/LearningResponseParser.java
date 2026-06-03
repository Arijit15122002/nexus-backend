package com.arijit.nexus_backend.ai.response.parser.domain;

import com.arijit.nexus_backend.ai.response.dto.ResponseSection;
import com.arijit.nexus_backend.ai.response.entity.SectionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class LearningResponseParser
        implements DomainResponseParser {

    @Override
    public List<ResponseSection> parse(
            String rawResponse
    ) {

        List<ResponseSection> sections =
                new ArrayList<>();

        String[] blocks =
                rawResponse.split(
                        "(?=\\*\\*Days)|(?=##)|(?=Phase)"
                );

        int order = 1;

        for (String block : blocks) {

            if (block.isBlank()) {
                continue;
            }

            SectionType sectionType =
                    determineLearningSectionType(
                            block
                    );

            sections.add(

                    ResponseSection.builder()

                            .sectionType(
                                    sectionType
                            )

                            .title(
                                    extractTitle(block)
                            )

                            .content(
                                    block.trim()
                            )

                            .order(order)

                            .importance(
                                    10 - Math.min(order, 5)
                            )

                            .build()

            );

            order++;

        }

        return sections;

    }

    // =========================
    // DETECT SECTION TYPE
    // =========================

    private SectionType determineLearningSectionType(
            String block
    ) {

        String lower =
                block.toLowerCase();

        if (
                lower.contains("phase")
                        || lower.contains("week")
                        || lower.contains("day")
        ) {

            return SectionType.ROADMAP;

        }

        if (
                lower.contains("resource")
                        || lower.contains("youtube")
                        || lower.contains("course")
        ) {

            return SectionType.RESOURCE;

        }

        if (
                lower.contains("timeline")
                        || lower.contains("month")
        ) {

            return SectionType.TIMELINE;

        }

        return SectionType.TEXT;

    }

    // =========================
    // TITLE EXTRACTION
    // =========================

    private String extractTitle(
            String block
    ) {

        String[] lines =
                block.split("\n");

        if (lines.length > 0) {

            return lines[0]
                    .replace("#", "")
                    .replace("*", "")
                    .trim();

        }

        return "Learning Section";

    }

}