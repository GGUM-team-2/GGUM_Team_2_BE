package com.example.ggum.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatRoomResponse {
    private Long roomId;
    private String roomName;
    private Long postId;
    private String postTitle;
    private Long participantCount;
    private Long participantLimit;
    private String latestMessage;
}
