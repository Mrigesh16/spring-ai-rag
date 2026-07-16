package com.mrigesh.springairag.service;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PdfService {

  private final VectorStore vectorStore;

  private final Tika tika = new Tika();

  public void upload(MultipartFile file) throws IOException, TikaException {

    String text = tika.parseToString(file.getInputStream());

    TokenTextSplitter splitter = new TokenTextSplitter();

    List<Document> chunks = splitter.apply(List.of(new Document(text)));
    vectorStore.add(chunks);
  }
}
