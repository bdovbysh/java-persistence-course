package com.bobocode.model;

import com.bobocode.util.ExerciseNotCompletedException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * todo:
 * - make a setter for field {@link Photo#comments} {@code private}
 * - implement equals() and hashCode() based on identifier field
 *
 * - configure JPA entity
 * - specify table name: "photo"
 * - configure auto generated identifier
 * - configure not nullable and unique column: url
 *
 * - initialize field comments
 * - map relation between Photo and PhotoComment on the child side
 * - implement helper methods {@link Photo#addComment(PhotoComment)} and {@link Photo#removeComment(PhotoComment)}
 * - enable cascade type {@link javax.persistence.CascadeType#ALL} for field {@link Photo#comments}
 * - enable orphan removal
 */
@Getter
@Setter
@Entity
@Table(name = "photo")
@EqualsAndHashCode(of = "id")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String url;

    private String description;

    @OneToMany(mappedBy = "photo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Setter(AccessLevel.PRIVATE)
    private List<PhotoComment> comments = new ArrayList<>();

    public void addComment(PhotoComment comment) {
        comment.setPhoto(this);
        getComments().add(comment);
    }

    public void removeComment(PhotoComment comment) {
        comment.setPhoto(null);
        getComments().remove(comment);
    }
}
