package com.arijit.nexus_backend.ai.artifact.service;

import com.arijit.nexus_backend.ai.artifact.entity.ArtifactType;
import org.springframework.stereotype.Service;

@Service
public class ArtifactPathResolverService {

    public String resolvePath(

            String fileName,

            ArtifactType artifactType

    ) {

        if (
                fileName == null
                        || artifactType == null
        ) {

            return "misc/";

        }

        return switch (artifactType) {

            // =========================
            // BACKEND
            // =========================

            case BACKEND_SOURCE ->
                    resolveBackendPath(fileName);

            // =========================
            // FRONTEND
            // =========================

            case FRONTEND_SOURCE ->
                    resolveFrontendPath(fileName);

            // =========================
            // DATABASE
            // =========================

            case DATABASE_SCHEMA ->
                    "database/";

            // =========================
            // DEPLOYMENT
            // =========================

            case DEPLOYMENT_CONFIG ->
                    "deployment/";

            // =========================
            // CONFIG
            // =========================

            case CONFIGURATION ->
                    "config/";

            // =========================
            // DOCS
            // =========================

            case DOCUMENTATION ->
                    "docs/";

            default ->
                    "misc/";

        };

    }

    // =========================
    // BACKEND PATH
    // =========================

    private String resolveBackendPath(
            String fileName
    ) {

        String lower =
                fileName.toLowerCase();

        if (
                lower.contains("controller")
        ) {

            return "backend/controller/";

        }

        if (
                lower.contains("service")
        ) {

            return "backend/service/";

        }

        if (
                lower.contains("repository")
        ) {

            return "backend/repository/";

        }

        if (
                lower.contains("config")
        ) {

            return "backend/config/";

        }

        if (
                lower.contains("entity")
        ) {

            return "backend/entity/";

        }

        if (
                lower.contains("dto")
        ) {

            return "backend/dto/";

        }

        return "backend/common/";

    }

    // =========================
    // FRONTEND PATH
    // =========================

    private String resolveFrontendPath(
            String fileName
    ) {

        String lower =
                fileName.toLowerCase();

        if (
                lower.contains("component")
        ) {

            return "frontend/components/";

        }

        if (
                lower.contains("service")
        ) {

            return "frontend/services/";

        }

        if (
                lower.contains("page")
        ) {

            return "frontend/pages/";

        }

        return "frontend/common/";

    }

}