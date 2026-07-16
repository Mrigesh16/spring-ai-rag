package com.mrigesh.springairag.controller;

import com.mrigesh.springairag.dto.UploadResponse;
import com.mrigesh.springairag.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class PdfController {

  private final PdfService pdfService;

  @PostMapping("/upload")
  public ResponseEntity<UploadResponse> upload(@RequestParam MultipartFile file) throws Exception {

    pdfService.upload(file);

    return ResponseEntity.ok(new UploadResponse("Uploaded Successfully"));
  }
}
