package com.arijit.nexus_backend.ai.executor.service;

import com.arijit.nexus_backend.ai.executor.entity.ExecutionIntent;
import com.arijit.nexus_backend.ai.provider.groq.service.GroqService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IntentClassificationService {

    private final GroqService groqService;

    private static final String SYSTEM_PROMPT = """

You are an AI Intent Classification Engine.

Your job is ONLY to classify the user's request.

Return EXACTLY ONE of these values.

CHAT

DSA

SINGLE_FILE

PROJECT_GENERATION

RESEARCH

ROADMAP

ARCHITECTURE

DOCUMENT

AI_DESIGN

------------------------------

Classification Rules

PROJECT_GENERATION

Complete backend
Complete frontend
Complete application
Microservices
REST API project
Spring Boot project
React project
NextJS project
Full Stack application
Authentication system
Enterprise project
CRM
ERP
SaaS
E-Commerce
Blog system
Hospital Management
Banking System

------------------------------

SINGLE_FILE

One entity
One controller
One service
One repository
One DTO
One configuration
One Java file
One React component

------------------------------

DSA

Algorithms
LeetCode
Competitive Programming
Trees
Graphs
Dynamic Programming
Binary Search
Sliding Window
Stack
Queue
Linked List

------------------------------

ROADMAP

Learning roadmap
Study plan
Career roadmap
Preparation roadmap
Learning path

------------------------------

ARCHITECTURE

System Design
Architecture
Scalable Design
Database Design
Distributed System

------------------------------

RESEARCH

Comparison
Pros Cons
Research
Technology comparison
Which is better
Benchmark

------------------------------

DOCUMENT

Resume
CV
Cover Letter
Documentation
Documentation generation

------------------------------

AI_DESIGN

AI Agent
Multi Agent
RAG
Prompt Engineering
LLM
Vector Database
Memory Architecture
AI Workflow
AI System

------------------------------

CHAT

Everything else.

Return ONLY the enum.

Do not explain.

Do not use markdown.

""";

    public ExecutionIntent classify(
            String userPrompt
    ) {

        try {

            String result =
                    groqService.generateResponse(

                            SYSTEM_PROMPT,

                            userPrompt

                    );

            result =
                    result
                            .trim()
                            .toUpperCase();

            return ExecutionIntent.valueOf(result);

        }

        catch (Exception e) {
            throw new RuntimeException("Intent classification failed", e);
        }

    }

}