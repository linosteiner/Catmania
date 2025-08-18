package ch.linosteiner.catmania.entities;

import io.micronaut.data.annotation.Embeddable;
import io.micronaut.data.annotation.MappedProperty;

import java.util.Objects;

@Embeddable
public class CatFriendshipId {
    @MappedProperty("fk_cat")
    private Long cat;

    @MappedProperty("fk_friend")
    private Long friend;

    public CatFriendshipId() {
    }

    public CatFriendshipId(Long cat, Long friend) {
        if (cat.equals(friend)) {
            throw new IllegalArgumentException("Cat cannot befriend itself");
        }
        if (cat < friend) {
            this.cat = cat;
            this.friend = friend;
        } else {
            this.cat = friend;
            this.friend = cat;
        }
    }

    public Long getCat() {
        return cat;
    }

    public Long getFriend() {
        return friend;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CatFriendshipId that = (CatFriendshipId) o;
        return Objects.equals(cat, that.cat) && Objects.equals(friend, that.friend);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cat, friend);
    }
}
