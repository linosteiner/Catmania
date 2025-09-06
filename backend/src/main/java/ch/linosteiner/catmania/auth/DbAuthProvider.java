package ch.linosteiner.catmania.auth;

import io.micronaut.core.annotation.Blocking;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailed;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.AuthenticationProvider;
import jakarta.inject.Singleton;

import java.util.List;


@Singleton
public class DbAuthProvider implements AuthenticationProvider<HttpRequest<?>, String, String> {

    private final AppUserRepository users;
    private final PasswordHasher.Hasher hasher;

    public DbAuthProvider(AppUserRepository users, PasswordHasher.Hasher hasher) {
        this.users = users;
        this.hasher = hasher;
    }

    @Override
    @Blocking
    public AuthenticationResponse authenticate(HttpRequest<?> request,
                                               AuthenticationRequest<String, String> authRequest) {
        final String username = authRequest.getIdentity();
        final String rawPassword = authRequest.getSecret();

        return users.findByUsername(username)
                .filter(u -> Boolean.TRUE.equals(u.getEnabled()))
                .filter(u -> hasher.matches(rawPassword, u.getPasswordHash()))
                .map(u -> {
                    List<String> roles = users.findRolesByUserId(u.getId());
                    return AuthenticationResponse.success(u.getUsername(), roles);
                })
                .orElseGet(AuthenticationFailed::new);
    }
}
