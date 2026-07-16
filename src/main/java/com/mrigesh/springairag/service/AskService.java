package com.mrigesh.springairag.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AskService {

  private final VectorStore vectorStore;
  private final ChatModel chatModel;

  public String ask(String question) {

    List<Document> documents =
        vectorStore.similaritySearch(SearchRequest.builder().query(question).topK(3).build());

    String context = documents.stream().map(Document::getText).collect(Collectors.joining("\n\n"));

    String systemPrompt =
        """
                You are a helpful AI assistant.

                Answer ONLY using the provided context.

                If the answer is not present in the context,
                say "I don't have enough information."

                Context:
                %s
                """
            .formatted(context);

    Prompt prompt = new Prompt(List.of(new SystemMessage(systemPrompt), new UserMessage(question)));

    return chatModel.call(prompt).getResult().getOutput().getText();
  }
}
