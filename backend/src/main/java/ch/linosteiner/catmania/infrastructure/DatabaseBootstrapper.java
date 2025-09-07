package ch.linosteiner.catmania.infrastructure;

import io.micronaut.context.annotation.Context;
import io.micronaut.core.annotation.Order;
import io.micronaut.core.order.Ordered;
import io.micronaut.data.connection.annotation.Connectable;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

@Singleton
@Context
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DatabaseBootstrapper {
    private static final String TARGET_DB = "catmania";
    private static final String OWNER = System.getenv().getOrDefault("PGUSER", "postgres");
    private final DataSource dataSource;

    public DatabaseBootstrapper(@Named("bootstrap") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    void init() {
        ensureDatabaseExists();
    }

    @Connectable("bootstrap")
    public void ensureDatabaseExists() {
        try (Connection connection = dataSource.getConnection()) {
            boolean exists;
            try (PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM pg_database WHERE datname = ?")) {
                statement.setString(1, TARGET_DB);
                exists = statement.executeQuery().next();
            }
            if (!exists) {
                try (Statement statement = connection.createStatement()) {
                    statement.execute("CREATE DATABASE " + TARGET_DB + " OWNER " + OWNER);
                    statement.execute("GRANT ALL PRIVILEGES ON DATABASE " + TARGET_DB + " TO " + OWNER);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
