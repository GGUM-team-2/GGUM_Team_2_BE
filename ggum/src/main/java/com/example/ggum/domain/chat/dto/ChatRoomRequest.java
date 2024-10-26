package com.example.ggum.domain.chat.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomRequest {

    private String roomName;
    private Long creatorId;
    private List<Long> userIds;
    private Long postId;
}
