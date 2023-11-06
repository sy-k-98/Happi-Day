package com.happiday.Happi_Day.domain.controller;

import com.happiday.Happi_Day.domain.entity.chat.ChatRoom;
import com.happiday.Happi_Day.domain.entity.chat.dto.ChatMessageDto;
import com.happiday.Happi_Day.domain.entity.chat.dto.ChatNicknameDto;
import com.happiday.Happi_Day.domain.entity.chat.dto.ChatRoomResponse;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.entity.user.dto.UserResponseDto;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.domain.repository.ChatRoomRepository;
import com.happiday.Happi_Day.domain.service.ChatRoomService;
import com.happiday.Happi_Day.domain.service.ChatService;
import com.happiday.Happi_Day.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomService chatRoomService;
    private final ChatService chatService;

    // 채팅방 만들기
    @PostMapping
    public ResponseEntity<Long> createChatRoom(@RequestBody ChatNicknameDto dto) {
        String username = SecurityUtils.getCurrentUsername();
        User receiver = userRepository.findByNickname(dto.getNickname()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Long roomId = chatRoomService.createChatRoom(receiver, username);
        return new ResponseEntity<>(roomId, HttpStatus.CREATED);
    }

    // 채팅방 리스트 찾기
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getChatRooms() {
        String username = SecurityUtils.getCurrentUsername();
        List<ChatRoomResponse> chatRoomResponses = chatRoomService.findChatRooms(username);
        return new ResponseEntity<>(chatRoomResponses, HttpStatus.OK);
    }

    // 채팅방 하나 찾기 -> 내역 가져오기
    @GetMapping("/{roomId}")
    public ResponseEntity<List<ChatMessageDto>> getChatRoom(@PathVariable("roomId") Long roomId) {
        String username = SecurityUtils.getCurrentUsername();
        return new ResponseEntity<>(chatService.getChatMessages(username, roomId), HttpStatus.OK);
    }

    @GetMapping("/findAllUser")
    public ResponseEntity<List<UserResponseDto>> getNicknameList() {
        String username = SecurityUtils.getCurrentUsername();
        List<User> userList = userRepository.findAllByUsernameNot(username);
        return new ResponseEntity<>(userList.stream().map(UserResponseDto::fromEntity).collect(Collectors.toList()), HttpStatus.OK);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteChatRoom(@PathVariable("roomId") Long roomId) {
        String username = SecurityUtils.getCurrentUsername();
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        chatRoomService.deleteChatRoom(username, chatRoom);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
