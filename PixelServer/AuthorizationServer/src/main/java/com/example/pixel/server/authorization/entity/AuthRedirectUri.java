package com.example.pixel.server.authorization.entity;

import com.example.pixel.server.util.entity.BaseEntity;
import com.example.pixel.server.util.entity.BaseNameEntity;
import com.example.pixel.server.util.entity.EntityAsIdOnlySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Objects;

@Entity(name = "redirect_uri")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRedirectUri implements BaseEntity, BaseNameEntity {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String name;

    @JsonSerialize(using = EntityAsIdOnlySerializer.class)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AuthClient client;

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthRedirectUri other = (AuthRedirectUri) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return "AuthRedirectUri(" + name + ")";
    }

}
