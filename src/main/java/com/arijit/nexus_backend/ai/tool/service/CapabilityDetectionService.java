package com.arijit.nexus_backend.ai.tool.service;

import com.arijit.nexus_backend.ai.tool.dto.CapabilityDetectionResult;
import com.arijit.nexus_backend.ai.tool.entity.ToolCapability;
import com.arijit.nexus_backend.ai.tool.entity.ToolDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CapabilityDetectionService {

    private final CapabilityDomainService capabilityDomainService;

    public CapabilityDetectionResult detectCapability(
            String message
    ) {

        String lower =
                message.toLowerCase();

        ToolCapability capability;

        // CODE

        if (
                lower.contains("spring boot")
                        || lower.contains("react")
                        || lower.contains("generate code")
                        || lower.contains("write code")
                        || lower.contains("entity")
                        || lower.contains("api")
        ) {

            capability =
                    ToolCapability.CODE_GENERATION;

        }

        // BUG FIXING

        else if (
                lower.contains("bug")
                        || lower.contains("error")
                        || lower.contains("exception")
                        || lower.contains("fix")
                        || lower.contains("not working")
        ) {

            capability =
                    ToolCapability.BUG_FIXING;

        }

        // ARCHITECTURE

        else if (
                lower.contains("architecture")
                        || lower.contains("system design")
                        || lower.contains("scalable")
        ) {

            capability =
                    ToolCapability.ARCHITECTURE_DESIGN;

        }

        // ROADMAP

        else if (
                lower.contains("roadmap")
                        || lower.contains("learning path")
                        || lower.contains("study plan")
        ) {

            capability =
                    ToolCapability.ROADMAP_GENERATION;

        }

        // RESUME

        else if (
                lower.contains("resume")
                        || lower.contains("cv")
        ) {

            capability =
                    ToolCapability.RESUME_GENERATION;

        }

        // RESEARCH

        else if (
                lower.contains("research")
                        || lower.contains("compare")
                        || lower.contains("comparison")
        ) {

            capability =
                    ToolCapability.WEB_RESEARCH;

        }

        // AI

        else if (
                lower.contains("rag")
                        || lower.contains("ai agent")
                        || lower.contains("orchestrator")
        ) {

            capability =
                    ToolCapability.AI_AGENT_DESIGN;

        }

        // DEFAULT

        else {

            capability =
                    ToolCapability.GENERAL_CHAT;

        }

        ToolDomain domain =
                capabilityDomainService
                        .resolveDomain(capability);

        return new CapabilityDetectionResult(
                capability,
                domain
        );

    }

}