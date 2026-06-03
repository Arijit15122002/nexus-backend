package com.arijit.nexus_backend.ai.response.parser.domain;

import com.arijit.nexus_backend.ai.response.dto.ResponseSection;
import com.arijit.nexus_backend.ai.response.entity.SectionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class CodeResponseParser
        implements DomainResponseParser {

    private static final Pattern FILE_PATTERN =
            Pattern.compile(
                    "FILE:\\s*(.*?)\\R\\s*LANGUAGE:\\s*(.*?)\\R\\s*```(.*?)\\R(.*?)```",
                    Pattern.DOTALL | Pattern.CASE_INSENSITIVE
            );

    @Override
    public List<ResponseSection> parse(
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

        Matcher matcher =
                FILE_PATTERN.matcher(rawResponse);

        log.info(
                "RAW RESPONSE LENGTH = {}",
                rawResponse.length()
        );

        log.info(
                "FILE MATCH FOUND = {}",
                matcher.find()
        );

        matcher.reset();

        int order = 1;

        while (matcher.find()) {

            String filePath =
                    matcher.group(1).trim();

            String language =
                    matcher.group(2).trim().toLowerCase();

            String code =
                    matcher.group(4).trim();

            sections.add(

                    ResponseSection.builder()

                            .sectionType(
                                    SectionType.CODE
                            )

                            .title(
                                    filePath
                            )

                            .content(
                                    code
                            )

                            .language(
                                    language
                            )

                            .order(
                                    order++
                            )

                            .importance(10)

                            .collapsible(true)

                            .searchable(true)

                            .exportable(true)

                            .renderMode(
                                    "CODE_EDITOR"
                            )

                            .build()

            );

        }

        /*
         * fallback
         */

        if (sections.isEmpty()) {

            sections.add(

                    ResponseSection.builder()

                            .sectionType(
                                    SectionType.TEXT
                            )

                            .title(
                                    "Response"
                            )

                            .content(
                                    rawResponse
                            )

                            .order(1)

                            .importance(10)

                            .renderMode(
                                    "TEXT_VIEW"
                            )

                            .build()

            );

        }

        log.info(
                "Parsed {} code artifacts",
                sections.size()
        );

        return sections;

    }

}