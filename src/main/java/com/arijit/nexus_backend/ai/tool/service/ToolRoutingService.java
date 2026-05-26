package com.arijit.nexus_backend.ai.tool.service;

import com.arijit.nexus_backend.ai.tool.dto.CapabilityDetectionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ToolRoutingService {

    private final CapabilityDetectionService
            capabilityDetectionService;

    public CapabilityDetectionResult route(
            String message
    ) {

        return capabilityDetectionService
                .detectCapability(message);

    }

}