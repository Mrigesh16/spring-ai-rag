package com.mrigesh.springairag.controller;

import com.mrigesh.springairag.dto.ChatRequest;
import com.mrigesh.springairag.dto.ChatResponse;
import com.mrigesh.springairag.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

  private final ChatService chatService;

  @PostMapping
  public ChatResponse chat(@RequestBody ChatRequest request) {

    return new ChatResponse(chatService.chat(request.message()));
  }
}
