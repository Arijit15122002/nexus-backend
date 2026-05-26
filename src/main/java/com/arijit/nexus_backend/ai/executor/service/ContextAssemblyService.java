package com.arijit.nexus_backend.ai.executor.service;

import org.springframework.stereotype.Service;

@Service
public class ContextAssemblyService {

    public String assembleContext(

            String memoryContext,

            String recentMessages,

            String summaries

    ) {

        return """

                ================= MEMORY =================

                %s

                ================= RECENT =================

                %s

                ================= SUMMARIES =================

                %s

                """.formatted(

                memoryContext,

                recentMessages,

                summaries

        );

    }

}