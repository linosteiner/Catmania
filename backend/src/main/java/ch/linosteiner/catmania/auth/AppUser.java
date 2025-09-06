package ch.linosteiner.catmania.auth;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;

@MappedEntity("app_user")
@Introspected
public class AppUser {
    @Id
    @GeneratedValue
    private Long id;
    private String username;
    @MappedProperty("password_hash")
    private String passwordHash;
    private Boolean enabled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Boolean getEnabled() {
        return enabled;
    }

}
