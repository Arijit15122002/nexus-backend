package com.arijit.nexus_backend.ai.response.dto;

import com.arijit.nexus_backend.ai.response.entity.SectionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseSection {

    private SectionType sectionType;

    private String title;

    private String content;

    private Integer order;

    private Integer importance;

    private String language;

    private Boolean collapsible;

    private Boolean searchable;

    private Boolean exportable;

    private String icon;

    private String renderMode;

}