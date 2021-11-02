package com.sabd.fileserver;

import java.security.NoSuchAlgorithmException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.r2dbc.core.DatabaseClient;

import com.sabd.fileserver.model.ChunkEntity;

import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class FileserverApplication {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        SpringApplication.run(FileserverApplication.class, args);
    }

    @Bean
    BeforeConvertCallback<ChunkEntity> idGeneratingCallback(DatabaseClient databaseClient) {

        return (customer, sqlIdentifier) -> {

            if (customer.getId() == null) {

                return databaseClient.sql("SELECT chunks_primary_key.nextval") //
                        .map(row -> row.get(0, Long.class)) //
                        .first() //
                        .map(customer::withId);
            }

            return Mono.just(customer);
        };
    }

    @Bean
    ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {

        var initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ByteArrayResource(("DROP FUNCTION IF EXISTS find_chunk_or_insert_new;"
                + "DROP TABLE IF EXISTS chunks;"
                + "DROP SEQUENCE IF EXISTS chunks_seq;"
                + "DROP TABLE IF EXISTS files;"
                + "DROP SEQUENCE IF EXISTS files_seq;"
                + "CREATE SEQUENCE IF NOT EXISTS chunks_seq MINVALUE 1 START WITH 1 INCREMENT BY 1;"
                + "CREATE SEQUENCE IF NOT EXISTS files_seq MINVALUE 1 START WITH 1 INCREMENT BY 1;"
                + "CREATE TABLE chunks (id INT PRIMARY KEY DEFAULT nextval('chunks_seq'), hash VARCHAR(1024), path VARCHAR(100), size INT, position INT,  count INT);"
                + "CREATE TABLE files (id INT PRIMARY KEY DEFAULT nextval('files_seq'), name VARCHAR(1024), uuid VARCHAR(1024), create_at timestamp);"
                + "CREATE UNIQUE INDEX chunks_unique_id on chunks (hash);"
                + "CREATE OR REPLACE FUNCTION find_chunk_or_insert_new(p_hash VARCHAR(1024), p_path VARCHAR(100), p_size INT, p_position INT)\n" +
                "RETURNS SETOF chunks\n" +
                "  LANGUAGE plpgsql AS\n" +
                "'\n" +
                "declare\n" +
                "   exist_row integer;\n" +
                "BEGIN\n" +
                "    select id\n" +
                "   into exist_row\n" +
                "   from chunks where chunks.hash=p_hash;\n" +
                "    IF exist_row > 0 THEN\n" +
                "        UPDATE chunks SET count=count+1 WHERE hash=p_hash;\n" +
                "    ELSE\n" +
                "        INSERT INTO chunks (hash, path, position, size, count)\n" +
                "        VALUES (p_hash, p_path, p_position, p_size, 1);\n" +
                "    END IF;\n" +
                "   RETURN QUERY select *\n" +
                "   from chunks where chunks.hash=p_hash LIMIT 1;\n" +
                "END\n" +
                "';")
                .getBytes())));

        return initializer;
    }

}
