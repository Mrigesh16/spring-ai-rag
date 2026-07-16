package com.mrigesh.springairag.controller;

import com.mrigesh.springairag.dto.AskRequest;
import com.mrigesh.springairag.service.RetrievalService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/retrieve")
@RequiredArgsConstructor
public class RetrievalController {

  private final RetrievalService retrievalService;

  @PostMapping
  public List<Document> retrieve(@RequestBody AskRequest request) {

    return retrievalService.search(request.question());
  }
}
