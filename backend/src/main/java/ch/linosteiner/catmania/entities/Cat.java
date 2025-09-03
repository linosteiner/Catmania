package ch.linosteiner.catmania.entities;

import io.micronaut.data.annotation.*;
import io.micronaut.data.annotation.sql.JoinColumn;
import io.micronaut.data.annotation.sql.JoinTable;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static io.micronaut.data.annotation.Relation.Kind.MANY_TO_MANY;

@Serdeable
@MappedEntity("cat")
public class Cat {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @MappedProperty("birth_date")
    private LocalDate birthDate;

    @Relation(Relation.Kind.MANY_TO_ONE)
    @NotNull
    private Breed breed;

    @Relation(MANY_TO_MANY)
    @JoinTable(name = "cat_behaviour")
    private Set<Behaviour> behaviours;

    @Relation(MANY_TO_MANY)
    @JoinTable(
            name = "cat_friendship",
            joinColumns = @JoinColumn(name = "cat_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<Cat> friends;

    @DateCreated
    @MappedProperty("created_at")
    private LocalDateTime createdDate;

    @DateUpdated
    @MappedProperty("updated_at")
    private LocalDateTime updatedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Breed getBreed() {
        return breed;
    }

    public void setBreed(Breed breed) {
        this.breed = breed;
    }

    public Set<Behaviour> getBehaviours() {
        return behaviours;
    }

    public void setBehaviours(Set<Behaviour> behaviours) {
        this.behaviours = behaviours;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
}
