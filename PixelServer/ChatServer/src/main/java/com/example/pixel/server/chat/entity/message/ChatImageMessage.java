package com.example.pixel.server.chat.entity.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

import java.net.URL;

@Entity(name = "chat_image_message")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatImageMessage extends ChatMessage {

    @Column(nullable = false)
    private URL url;

    @JsonProperty("type")
    private String getTypeForJSON() {
        return "image";
    }

}
