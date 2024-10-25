package com.example.ggum.domain.chat.controller;

import com.example.ggum.domain.chat.dto.ChatRoomRequest;
import com.example.ggum.domain.chat.dto.ChatRoomResponse;
import com.example.ggum.domain.chat.entity.ChatRoom;
import com.example.ggum.domain.chat.serivce.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
@Tag(name = "ChatRoom API", description = "채팅방 관련 API")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    //새로운 채팅방 생성
    @PostMapping("/room")
    @Operation(summary = "새로운 채팅방 생성", description = "채팅방을 생성 + 참여할 초긴 유저 초대")
    public ResponseEntity<ChatRoom> createChatRoom(@RequestBody ChatRoomRequest chatRoomRequest) {
        ChatRoom chatRoom = chatRoomService.createChatRoom(
                chatRoomRequest.getRoomName(),
                chatRoomRequest.getCreatorId(),
                chatRoomRequest.getPostId(),
                chatRoomRequest.getUserIds()
        );
        return ResponseEntity.ok(chatRoom);
    }

    //기존에 있는 채팅방에 새로운 유저 추가
    @Operation(summary = "기존 채팅방에 유저 추가", description = "기존의 채팅방에 새로운 유저를 초대")
    @PostMapping("/room/{roomId}/add-users")
    public ResponseEntity<String> addUserToChatRoom(@PathVariable("roomId") Long roomId, @RequestBody List<Long> userIds) {
        chatRoomService.addUserToChatRoom(roomId, userIds);
        return ResponseEntity.ok("새로운 유저를 채팅방에 초대 성공");
    }

    //채팅방 리스트 조회 -> 전체 / 공동구매 / 나눔 최신순으로 정렬하도록!
    @Operation(summary = "채팅방 리스트 조회", description = "유저가 속한 채팅방들을 조회 공동구매, 나눔, 전체를 최신순으로 정렬")
    @GetMapping("/user/{userId}/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getUserChatRooms(
            @PathVariable("userId") Long userId,
            @RequestParam(name = "postTypeFilter",required = false) String postTypeFilter) {

        // postTypeFilter가 없으면 전체 조회
        List<ChatRoomResponse> chatRooms = chatRoomService.getUserChatRooms(userId, postTypeFilter);
        return ResponseEntity.ok(chatRooms);
    }


}
