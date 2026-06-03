package com.arijit.nexus_backend.ai.artifact.structure;

import com.arijit.nexus_backend.ai.artifact.dto.CodeArtifact;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectStructureBuilderService {

    public ProjectStructureNode buildStructure(
            List<CodeArtifact> artifacts
    ) {

        ProjectStructureNode root =
                ProjectStructureNode.builder()

                        .name("project-root")

                        .path("/")

                        .directory(true)

                        .build();

        for (CodeArtifact artifact : artifacts) {

            addArtifactToTree(
                    root,
                    artifact
            );

        }

        return root;

    }

    // =========================
    // ADD TO TREE
    // =========================

    private void addArtifactToTree(

            ProjectStructureNode root,

            CodeArtifact artifact

    ) {

        String[] paths =
                artifact.getFilePath()
                        .split("/");

        ProjectStructureNode current =
                root;

        String currentPath = "";

        for (String path : paths) {

            if (path.isBlank()) {
                continue;
            }

            currentPath += "/" + path;

            ProjectStructureNode existing =
                    findChild(
                            current,
                            path
                    );

            if (existing == null) {

                ProjectStructureNode newNode =
                        ProjectStructureNode.builder()

                                .name(path)

                                .path(currentPath)

                                .directory(true)

                                .build();

                current.getChildren()
                        .add(newNode);

                current = newNode;

            } else {

                current = existing;

            }

        }

        // =========================
        // FILE NODE
        // =========================

        current.getChildren()
                .add(

                        ProjectStructureNode.builder()

                                .name(
                                        artifact.getFileName()
                                )

                                .path(
                                        artifact.getFilePath()
                                                + artifact.getFileName()
                                )

                                .directory(false)

                                .build()

                );

    }

    // =========================
    // FIND CHILD
    // =========================

    private ProjectStructureNode findChild(

            ProjectStructureNode parent,

            String name

    ) {

        return parent.getChildren()

                .stream()

                .filter(

                        child ->

                                child.getName()
                                        .equals(name)

                )

                .findFirst()

                .orElse(null);

    }

}