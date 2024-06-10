package com.example.pixel.server.chat.entity.attachment;

import com.example.pixel.server.chat.entity.chat.ChatContact;
import com.example.pixel.server.util.entity.EntityAsIdOnlySerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity(name = "group_contact_attachment")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"user", "contact"})
)
public class ChatContactUserAttachment extends ChatUserAttachment {

    @JsonSerialize(using = EntityAsIdOnlySerializer.class)
    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ChatContact contact;

    @Override
    public ChatContact getChatRoom() {
        return contact;
    }

    @JsonProperty("type")
    private String getTypeForJSON() {
        return "contact";
    }

}
