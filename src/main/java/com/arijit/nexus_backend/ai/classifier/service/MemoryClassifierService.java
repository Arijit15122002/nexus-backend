package com.arijit.nexus_backend.ai.classifier.service;

import com.arijit.nexus_backend.ai.classifier.dto.MemoryClassificationResult;
import com.arijit.nexus_backend.message.entity.MemoryType;
import org.springframework.stereotype.Service;

@Service
public class MemoryClassifierService {

    public MemoryClassificationResult classify(String text) {

        // =========================
        // NULL SAFETY
        // =========================

        if (
                text == null
                        || text.trim().isEmpty()
        ) {

            return new MemoryClassificationResult(
                    MemoryType.TEMPORARY,
                    1
            );

        }

        String lower =
                text.toLowerCase().trim();

        // =========================
        // PERSONAL IDENTITY
        // =========================

        if (
                lower.contains("my name is")
                        || lower.contains("i am")
                        || lower.contains("i'm")
                        || lower.contains("call me")
                        || lower.contains("this is ")
        ) {

            return new MemoryClassificationResult(
                    MemoryType.PERSONAL,
                    10
            );

        }

        // =========================
        // AGE / LOCATION / PERSONAL INFO
        // =========================

        if (
                lower.contains("i live")
                        || lower.contains("i'm from")
                        || lower.contains("my age")
                        || lower.contains("i study")
                        || lower.contains("my college")
                        || lower.contains("my university")
                        || lower.contains("my birthday")
                        || lower.contains("my phone")
                        || lower.contains("my email")
        ) {

            return new MemoryClassificationResult(
                    MemoryType.PERSONAL,
                    9
            );

        }

        // =========================
        // USER PREFERENCES
        // =========================

        if (
                lower.contains("i like")
                        || lower.contains("i love")
                        || lower.contains("my favorite")
                        || lower.contains("prefer")
                        || lower.contains("i enjoy")
                        || lower.contains("i usually")
                        || lower.contains("i mostly use")
                        || lower.contains("my favourite")
        ) {

            return new MemoryClassificationResult(
                    MemoryType.PREFERENCE,
                    8
            );

        }

        // =========================
        // PROJECT / DEVELOPMENT
        // =========================

        if (
                lower.contains("my project")
                        || lower.contains("i'm building")
                        || lower.contains("i am building")
                        || lower.contains("developing")
                        || lower.contains("backend")
                        || lower.contains("frontend")
                        || lower.contains("spring boot")
                        || lower.contains("next.js")
                        || lower.contains("react")
                        || lower.contains("postgres")
                        || lower.contains("pgvector")
                        || lower.contains("vector db")
                        || lower.contains("rag")
                        || lower.contains("embedding")
                        || lower.contains("microservice")
                        || lower.contains("architecture")
                        || lower.contains("api")
                        || lower.contains("database")
                        || lower.contains("deployment")
                        || lower.contains("docker")
                        || lower.contains("kubernetes")
                        || lower.contains("redis")
                        || lower.contains("mongodb")
                        || lower.contains("prisma")
                        || lower.contains("system design")
        ) {

            return new MemoryClassificationResult(
                    MemoryType.PROJECT,
                    9
            );

        }

        // =========================
        // LONG TERM TASKS
        // =========================

        if (
                lower.contains("remember this")
                        || lower.contains("memorize this")
                        || lower.contains("don't forget")
                        || lower.contains("important")
                        || lower.contains("save this")
                        || lower.contains("note this")
        ) {

            return new MemoryClassificationResult(
                    MemoryType.PERSONAL,
                    10
            );

        }

        // =========================
        // CAREER / GOALS
        // =========================

        if (
                lower.contains("my goal")
                        || lower.contains("my dream")
                        || lower.contains("i want to become")
                        || lower.contains("i want to build")
                        || lower.contains("my future")
                        || lower.contains("my startup")
                        || lower.contains("my company")
                        || lower.contains("my career")
        ) {

            return new MemoryClassificationResult(
                    MemoryType.PERSONAL,
                    9
            );

        }

        // =========================
        // LEARNING CONTEXT
        // =========================

        if (
                lower.contains("teach me")
                        || lower.contains("help me learn")
                        || lower.contains("explain")
                        || lower.contains("i don't understand")
                        || lower.contains("how does")
                        || lower.contains("what is")
        ) {

            return new MemoryClassificationResult(
                    MemoryType.GENERAL,
                    5
            );

        }

        // =========================
        // CASUAL CHAT
        // =========================

        if (
                lower.equals("hi")
                        || lower.equals("hello")
                        || lower.equals("hey")
                        || lower.equals("yo")
                        || lower.equals("thanks")
                        || lower.equals("ok")
                        || lower.equals("okay")
                        || lower.equals("cool")
                        || lower.equals("nice")
        ) {

            return new MemoryClassificationResult(
                    MemoryType.TEMPORARY,
                    1
            );

        }

        // =========================
        // VERY SHORT MESSAGES
        // =========================

        if (
                lower.length() < 12
        ) {

            return new MemoryClassificationResult(
                    MemoryType.TEMPORARY,
                    2
            );

        }

        // =========================
        // QUESTIONS
        // =========================

        if (
                lower.endsWith("?")
        ) {

            return new MemoryClassificationResult(
                    MemoryType.GENERAL,
                    4
            );

        }

        // =========================
        // CODE / TECHNICAL CONTENT
        // =========================

        if (
                lower.contains("class ")
                        || lower.contains("public static")
                        || lower.contains("select * from")
                        || lower.contains("exception")
                        || lower.contains("error")
                        || lower.contains("stack trace")
                        || lower.contains("nullpointer")
                        || lower.contains("@service")
                        || lower.contains("@entity")
                        || lower.contains("@configuration")
        ) {

            return new MemoryClassificationResult(
                    MemoryType.PROJECT,
                    8
            );

        }

        // =========================
        // DEFAULT GENERAL MEMORY
        // =========================

        return new MemoryClassificationResult(
                MemoryType.GENERAL,
                5
        );

    }

}