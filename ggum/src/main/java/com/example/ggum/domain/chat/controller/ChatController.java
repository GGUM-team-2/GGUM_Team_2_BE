package com.example.ggum.domain.chat.controller;

import com.example.ggum.domain.chat.dto.ChatMessage;
import com.example.ggum.domain.chat.dto.InviteRequest;
import com.example.ggum.domain.chat.dto.MessageResponse;
import com.example.ggum.domain.chat.entity.Message;
import com.example.ggum.domain.chat.repository.MessageRepository;
import com.example.ggum.domain.chat.serivce.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Tag(name = "Chat API", description = "채팅 관련 API")
public class ChatController {

    private final ChatService chatService;
    
    //MessageMapping : 웹소켓 프로토콜 어노테이션
    @MessageMapping("/api/v1/chat/room/{roomId}/send")
    public void sendMessage(ChatMessage chatMessage) {
        chatService.sendMessage(chatMessage);
    }


    @MessageMapping("/api/v1/chat/room/{roomId}/leave")
    public void leaveRoom(@DestinationVariable("roomId") Long roomId,ChatMessage chatMessage) {
        chatService.handleLeave(chatMessage);
    }


    @MessageMapping("/api/v1/chat/room/{roomId}/invite")
    public void inviteUser(@DestinationVariable Long roomId, @Payload InviteRequest inviteRequest) {
        chatService.inviteUserToChatRoom(roomId, inviteRequest.getUserId());
    }

    // 채팅방의 모든 메시지 가져오기
    @Operation(summary = "채팅방 메시지 조회", description = "해당 채팅방의 이전 메시지들 조회")
    @GetMapping("/api/v1/chat/room/{roomId}/messages")
    public ResponseEntity<List<MessageResponse>> getChatRoomMessages(@PathVariable("roomId") Long roomId) {
        List<MessageResponse> messages = chatService.getChatRoomMessages(roomId);
        return ResponseEntity.ok(messages);
    }
}
