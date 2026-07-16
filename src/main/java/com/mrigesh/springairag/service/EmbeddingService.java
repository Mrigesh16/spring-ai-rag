package com.mrigesh.springairag.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmbeddingService {

  private final EmbeddingModel embeddingModel;

  public float[] generateEmbedding(String text) {
    return embeddingModel.embed(text);
  }
}
