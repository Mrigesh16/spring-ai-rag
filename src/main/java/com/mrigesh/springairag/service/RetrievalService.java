package com.mrigesh.springairag.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RetrievalService {

  private final VectorStore vectorStore;

  public List<Document> search(String question) {

    SearchRequest request = SearchRequest.builder().query(question).topK(3).build();

    return vectorStore.similaritySearch(request);
  }
}
