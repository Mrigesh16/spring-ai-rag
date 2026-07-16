package com.mrigesh.springairag.controller;

import com.mrigesh.springairag.dto.DocumentRequest;
import com.mrigesh.springairag.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

  private final DocumentService documentService;

  @PostMapping
  public ResponseEntity<String> add(@RequestBody DocumentRequest request) {

    documentService.addDocument(request.content());

    return ResponseEntity.ok("Document indexed successfully");
  }
}
