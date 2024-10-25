package com.example.ggum.domain.chat.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {//Client가 전송하는 메세지의 구조

    private Long messageId;
    private Long userId;
    private Long roomId;
    private String content;
    private String username;
    private MessageType type;

    //채팅방에 유저가 들어오거나 나갈때 알리는 기능을 구현하기 위해선 필요
    public enum MessageType{
        ENTER, CHAT, LEAVE
    }
}
