package com.arijit.nexus_backend.ai.tool.service;

import com.arijit.nexus_backend.ai.tool.entity.ToolCapability;
import com.arijit.nexus_backend.ai.tool.entity.ToolDomain;
import org.springframework.stereotype.Service;

@Service
public class CapabilityDomainService {

    public ToolDomain resolveDomain(
            ToolCapability capability
    ) {

        return switch (capability) {

            case CODE_GENERATION,
                 BUG_FIXING,
                 CODE_REVIEW,
                 CODE_EXPLANATION,
                 API_GENERATION,
                 DATABASE_DESIGN,
                 SYSTEM_DESIGN,
                 ARCHITECTURE_DESIGN,
                 TEST_GENERATION,
                 REFACTORING

                    -> ToolDomain.CODE;

            case RESUME_GENERATION,
                 COVER_LETTER_GENERATION,
                 DOCUMENT_SUMMARIZATION

                    -> ToolDomain.DOCUMENT;

            case ROADMAP_GENERATION,
                 LEARNING_GUIDANCE,
                 INTERVIEW_PREPARATION

                    -> ToolDomain.LEARNING;

            case TASK_PLANNING,
                 GOAL_BREAKDOWN,
                 PROJECT_PLANNING

                    -> ToolDomain.PRODUCTIVITY;

            case MEMORY_SEARCH,
                 CONVERSATION_SUMMARY

                    -> ToolDomain.MEMORY;

            case TECH_STACK_RECOMMENDATION,
                 WEB_RESEARCH,
                 COMPARISON_ANALYSIS

                    -> ToolDomain.RESEARCH;

            case PROMPT_ENGINEERING,
                 AI_AGENT_DESIGN,
                 RAG_ARCHITECTURE

                    -> ToolDomain.AI;

            case CONTENT_WRITING,
                 BLOG_GENERATION,
                 SOCIAL_MEDIA_CONTENT

                    -> ToolDomain.CREATIVE;

            default -> ToolDomain.GENERAL;

        };

    }

}