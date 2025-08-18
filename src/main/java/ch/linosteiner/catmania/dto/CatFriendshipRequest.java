package ch.linosteiner.catmania.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CatFriendshipRequest(
        @NotNull
        @Positive
        Long cat,
        @NotNull
        @Positive
        Long friend
) {
    public CatFriendshipRequest {
        if (cat.equals(friend)) {
            throw new IllegalArgumentException("Cat cannot befriend itself");
        }
        if (cat > friend) {
            long tmp = cat;
            cat = friend;
            friend = tmp;
        }
    }
}
