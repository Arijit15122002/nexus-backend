package com.arijit.nexus_backend.ai.config;

import com.pgvector.PGvector;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.postgresql.PGConnection;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;

@Configuration
@RequiredArgsConstructor
public class PgVectorConfig {

    private final DataSource dataSource;

    @PostConstruct
    public void init() {

        try (
                Connection connection =
                        dataSource.getConnection()
        ) {

            PGConnection pgConnection =
                    connection.unwrap(PGConnection.class);

            pgConnection.addDataType(
                    "vector",
                    PGvector.class
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to register pgvector",
                    e
            );

        }

    }
}