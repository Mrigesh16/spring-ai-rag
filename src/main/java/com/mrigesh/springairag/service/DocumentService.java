package com.mrigesh.springairag.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentService {

  private final VectorStore vectorStore;

  public void addDocument(String content) {

    Document document = new Document(content);

    vectorStore.add(List.of(document));
  }
}
