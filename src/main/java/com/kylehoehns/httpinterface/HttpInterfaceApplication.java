package com.kylehoehns.httpinterface;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class HttpInterfaceApplication {

  public static void main(String[] args) {
    SpringApplication.run(HttpInterfaceApplication.class, args);
  }

}

@Configuration
class HttpConfiguration {

  @Bean
  RestClient restClient() {
    return RestClient.builder()
        .defaultHeader("my-api-key", "abc-123")
        .baseUrl("https://jsonplaceholder.typicode.com")
        .build();
  }

}

@Service
class PostsService {

  private final RestClient restClient;

  public PostsService(RestClient restClient) {
    this.restClient = restClient;
  }

  public Post getPost(int id) {
    return restClient
        .get()
        .uri("https://jsonplaceholder.typicode.com/posts/{id}", id)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .body(Post.class);
  }

}

record Post(int userId, int id, String title, String body) {
}

@RestController
@RequestMapping
class PostsRestController {

  private final PostsService postsService;

  public PostsRestController(PostsService postsService) {
    this.postsService = postsService;
  }

  @GetMapping("/posts/{id}")
  public Post getPost(@PathVariable int id) {
    return postsService.getPost(id);
  }

}

