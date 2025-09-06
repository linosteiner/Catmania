package ch.linosteiner.catmania.auth;

import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@Factory
public class PasswordHasher {
    private static final Mode MODE = Mode.SHA256;

    @Singleton
    public Hasher hasher() {
        return switch (MODE) {
            case PLAIN -> new Hasher() {
                public String encode(String raw) {
                    return raw;
                }

                public boolean matches(String raw, String stored) {
                    return stored != null && stored.equals(raw);
                }
            };
            case SHA256 -> new Hasher() {
                public String encode(String raw) {
                    try {
                        MessageDigest md = MessageDigest.getInstance("SHA-256");
                        return HexFormat.of().formatHex(md.digest(raw.getBytes(StandardCharsets.UTF_8)));
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }

                public boolean matches(String raw, String stored) {
                    return stored != null && stored.equals(encode(raw));
                }
            };
        };
    }

    public enum Mode {PLAIN, SHA256}

    public interface Hasher {

        boolean matches(String raw, String stored);
    }
}
