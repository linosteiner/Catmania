package ch.linosteiner.catmania.entities;

import io.micronaut.data.annotation.EmbeddedId;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
@MappedEntity("cat_friendship")
public class CatFriendship {
    @EmbeddedId
    private CatFriendshipId id;

    public CatFriendship() {
    }

    public CatFriendship(Long cat, Long friend) {
        this.id = new CatFriendshipId(cat, friend);
    }

    public CatFriendshipId getId() {
        return id;
    }

    public Long getCat() {
        return id.getCat();
    }

    public Long getFriend() {
        return id.getFriend();
    }
}

