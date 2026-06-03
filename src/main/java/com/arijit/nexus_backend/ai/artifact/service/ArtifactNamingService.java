package com.arijit.nexus_backend.ai.artifact.service;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ArtifactNamingService {

    public String generateFileName(

            String language,

            String content

    ) {

        if (
                language == null
                        || content == null
        ) {

            return "generated.txt";

        }

        return switch (language) {

            case "java" ->
                    generateJavaFileName(content);

            case "typescript" ->
                    generateTypeScriptFileName(content);

            case "javascript" ->
                    generateJavaScriptFileName(content);

            case "sql" ->
                    generateSqlFileName(content);

            case "yaml" ->
                    generateYamlFileName(content);

            case "dockerfile" ->
                    "Dockerfile";

            default ->
                    "generated.txt";

        };

    }

    // =========================
    // JAVA
    // =========================

    private String generateJavaFileName(
            String content
    ) {

        Pattern pattern =
                Pattern.compile(
                        "class\\s+(\\w+)"
                );

        Matcher matcher =
                pattern.matcher(content);

        if (matcher.find()) {

            return matcher.group(1)
                    + ".java";

        }

        return "GeneratedClass.java";

    }

    // =========================
    // TYPESCRIPT
    // =========================

    private String generateTypeScriptFileName(
            String content
    ) {

        if (
                content.contains("@Injectable")
        ) {

            return "generated.service.ts";

        }

        if (
                content.contains("@Component")
        ) {

            return "generated.component.ts";

        }

        return "generated.ts";

    }

    // =========================
    // JAVASCRIPT
    // =========================

    private String generateJavaScriptFileName(
            String content
    ) {

        if (
                content.contains("express")
        ) {

            return "server.js";

        }

        return "script.js";

    }

    // =========================
    // SQL
    // =========================

    private String generateSqlFileName(
            String content
    ) {

        Pattern pattern =
                Pattern.compile(
                        "CREATE TABLE\\s+(\\w+)",
                        Pattern.CASE_INSENSITIVE
                );

        Matcher matcher =
                pattern.matcher(content);

        if (matcher.find()) {

            return matcher.group(1)
                    + ".sql";

        }

        return "schema.sql";

    }

    // =========================
    // YAML
    // =========================

    private String generateYamlFileName(
            String content
    ) {

        if (
                content.contains("spring:")
        ) {

            return "application.yml";

        }

        if (
                content.contains("services:")
        ) {

            return "docker-compose.yml";

        }

        return "config.yml";

    }

}