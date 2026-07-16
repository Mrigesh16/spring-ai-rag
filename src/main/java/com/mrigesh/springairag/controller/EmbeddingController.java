package com.mrigesh.springairag.controller;

import com.mrigesh.springairag.dto.EmbeddingRequest;
import com.mrigesh.springairag.dto.EmbeddingResponse;
import com.mrigesh.springairag.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/embeddings")
@RequiredArgsConstructor
public class EmbeddingController {

  private final EmbeddingService embeddingService;

  @PostMapping
  public EmbeddingResponse embed(@RequestBody EmbeddingRequest request) {

    return new EmbeddingResponse(embeddingService.generateEmbedding(request.text()));
  }
}
