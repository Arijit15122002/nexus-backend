package com.arijit.nexus_backend.ai.response.parser.service;

import com.arijit.nexus_backend.ai.response.dto.ResponseSection;
import com.arijit.nexus_backend.ai.response.entity.SectionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ResponseParserService {

    public List<ResponseSection> parseSections(
            String rawResponse
    ) {

        List<ResponseSection> sections =
                new ArrayList<>();

        if (
                rawResponse == null
                        || rawResponse.isBlank()
        ) {

            return sections;

        }

        String[] blocks =
                rawResponse.split("(?=## )");

        int order = 1;

        for (String block : blocks) {

            if (block.isBlank()) {
                continue;
            }

            SectionType sectionType =
                    detectSectionType(block);

            String title =
                    extractTitle(block);

            sections.add(

                    ResponseSection.builder()

                            .sectionType(sectionType)

                            .title(title)

                            .content(block.trim())

                            .order(order)

                            .importance(
                                    calculateImportance(
                                            sectionType
                                    )
                            )

                            .language(
                                    detectLanguage(block)
                            )

                            .collapsible(
                                    sectionType == SectionType.CODE
                            )

                            .searchable(true)

                            .exportable(
                                    sectionType == SectionType.CODE
                                            || sectionType == SectionType.ARCHITECTURE
                            )

                            .icon(
                                    determineIcon(sectionType)
                            )

                            .renderMode(
                                    determineRenderMode(sectionType)
                            )

                            .build()

            );

            order++;

        }

        return sections;

    }

    // =========================
    // DETECT TYPE
    // =========================

    private SectionType detectSectionType(
            String block
    ) {

        String lower =
                block.toLowerCase();

        if (
                lower.contains("```java")
                        || lower.contains("```javascript")
                        || lower.contains("```typescript")
                        || lower.contains("```python")
                        || lower.contains("```sql")
                        || lower.contains("```dockerfile")
                        || lower.contains("```yaml")
        ) {

            return SectionType.CODE;

        }

        if (
                lower.contains("endpoint")
                        || lower.contains("/api/")
        ) {

            return SectionType.API;

        }

        if (
                lower.contains("create table")
                        || lower.contains("entity relationship")
                        || lower.contains("database schema")
                        || lower.contains("er diagram")
                        || lower.contains("primary key")
                        || lower.contains("foreign key")
        ) {

            return SectionType.DATABASE;

        }

        if (
                lower.contains("architecture")
                        || lower.contains("microservice")
                        || lower.contains("system design")
                        || lower.contains("folder structure")
        ) {

            return SectionType.ARCHITECTURE;

        }

        if (
                lower.contains("docker")
                        || lower.contains("deployment")
                        || lower.contains("kubernetes")
        ) {

            return SectionType.DEPLOYMENT;

        }

        if (
                lower.contains("roadmap")
                        || lower.contains("phase")
        ) {

            return SectionType.ROADMAP;

        }

        if (
                lower.contains("timeline")
                        || lower.contains("week")
                        || lower.contains("month")
        ) {

            return SectionType.TIMELINE;

        }

        if (
                lower.contains("resource")
                        || lower.contains("youtube")
                        || lower.contains("course")
        ) {

            return SectionType.RESOURCE;

        }

        if (
                lower.contains("- [ ]")
                        || lower.contains("checklist")
        ) {

            return SectionType.CHECKLIST;

        }

        if (
                lower.contains("warning")
                        || lower.contains("caution")
        ) {

            return SectionType.WARNING;

        }

        if (
                lower.contains("best practice")
        ) {

            return SectionType.BEST_PRACTICE;

        }

        return SectionType.TEXT;

    }

    private String determineIcon(
            SectionType sectionType
    ) {

        return switch (sectionType) {

            case CODE -> "code";

            case API -> "api";

            case DATABASE -> "database";

            case ARCHITECTURE -> "architecture";

            case DEPLOYMENT -> "deployment";

            case ROADMAP -> "roadmap";

            case TIMELINE -> "timeline";

            case RESOURCE -> "resource";

            case WARNING -> "warning";

            default -> "text";

        };

    }

    private String determineRenderMode(
            SectionType sectionType
    ) {

        return switch (sectionType) {

            case CODE -> "CODE_EDITOR";

            case API -> "API_BLOCK";

            case DATABASE -> "DATABASE_VIEW";

            case ARCHITECTURE -> "ARCHITECTURE_VIEW";

            case DEPLOYMENT -> "DEPLOYMENT_VIEW";

            case ROADMAP -> "ROADMAP_VIEW";

            case TIMELINE -> "TIMELINE_VIEW";

            case RESOURCE -> "RESOURCE_VIEW";

            case CHECKLIST -> "CHECKLIST_VIEW";

            default -> "TEXT_VIEW";

        };

    }

    // =========================
    // DETECT LANGUAGE
    // =========================

    private String detectLanguage(
            String block
    ) {

        String lower =
                block.toLowerCase();

        if (lower.contains("```java")) {
            return "java";
        }

        if (lower.contains("```javascript")) {
            return "javascript";
        }

        if (lower.contains("```python")) {
            return "python";
        }

        if (lower.contains("```sql")) {
            return "sql";
        }

        if (lower.contains("```dockerfile")) {
            return "dockerfile";
        }

        if (lower.contains("```yaml")) {
            return "yaml";
        }

        return null;

    }

    // =========================
    // TITLE
    // =========================

    private String extractTitle(
            String block
    ) {

        String[] lines =
                block.split("\n");

        if (lines.length > 0) {

            return lines[0]
                    .replace("#", "")
                    .trim();

        }

        return "AI Section";

    }

    // =========================
    // IMPORTANCE
    // =========================

    private Integer calculateImportance(
            SectionType sectionType
    ) {

        return switch (sectionType) {

            case CODE -> 10;

            case ARCHITECTURE -> 9;

            case DATABASE -> 9;

            case API -> 8;

            case DEPLOYMENT -> 8;

            case ROADMAP -> 8;

            case TIMELINE -> 7;

            case RESOURCE -> 6;

            case CHECKLIST -> 6;

            case WARNING -> 9;

            case BEST_PRACTICE -> 8;

            default -> 5;

        };

    }

}