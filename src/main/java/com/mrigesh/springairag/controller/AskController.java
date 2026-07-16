package com.mrigesh.springairag.controller;

import com.mrigesh.springairag.dto.AskRequest;
import com.mrigesh.springairag.service.AskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ask")
@RequiredArgsConstructor
public class AskController {

  private final AskService askService;

  @PostMapping
  public String ask(@RequestBody AskRequest request) {
    return askService.ask(request.question());
  }
}
