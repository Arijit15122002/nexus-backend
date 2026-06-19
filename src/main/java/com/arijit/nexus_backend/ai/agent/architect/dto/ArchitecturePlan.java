package com.arijit.nexus_backend.ai.agent.architect.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ArchitecturePlan {

    private String projectType;

    private String architectureStyle;

    private List<String> technologies;

    /*
     * Spring Boot -> 3.5.0
     * Java -> 21
     * PostgreSQL -> 17
     */
//    private Map<String, String> versions;

    /*
     * Controller
     * Service
     * Repository
     */
    private List<String> modules;

    /*
     * UserController.java
     * UserService.java
     */
    private List<String> files;

    /*
     * Layered
     * CQRS
     * Event Driven
     */
    private List<String> patterns;

    private String databaseStrategy;

    private String deploymentStrategy;

    private String securityStrategy;

    private String reasoning;

    private Map<String,String> requirements;

    private List<String> technologyRequirements;

//    public void updateVersions(
//            Map<String,String> versions
//    ) {
//
//        if (
//                this.versions == null
//        ) {
//
//            this.versions = versions;
//
//            return;
//
//        }
//
//        this.versions.putAll(
//                versions
//        );
//
//    }

}