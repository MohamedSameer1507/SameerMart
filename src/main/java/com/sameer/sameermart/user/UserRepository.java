package com.sameer.sameermart.user;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public UserRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public boolean existsByEmail(String email) {
        Boolean exists = jdbc.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM users WHERE email = :email)",
                Map.of("email", email.toLowerCase(Locale.ROOT)), Boolean.class);
        return Boolean.TRUE.equals(exists);
    }

    public void save(String name, String email, String passwordHash, UserRole role) {
        jdbc.update("""
                INSERT INTO users (name, email, password_hash, role)
                VALUES (:name, :email, :passwordHash, :role)
                """, Map.of(
                "name", name,
                "email", email.toLowerCase(Locale.ROOT),
                "passwordHash", passwordHash,
                "role", role.name()));
    }

    public Optional<StoredUser> findByEmail(String email) {
        return jdbc.query("""
                SELECT id, name, email, password_hash, role
                FROM users
                WHERE email = :email
                """, Map.of("email", email.toLowerCase(Locale.ROOT)),
                (resultSet, rowNum) -> new StoredUser(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("password_hash"),
                        UserRole.valueOf(resultSet.getString("role"))))
                .stream().findFirst();
    }

    public record StoredUser(long id, String name, String email, String passwordHash, UserRole role) {
    }
}
