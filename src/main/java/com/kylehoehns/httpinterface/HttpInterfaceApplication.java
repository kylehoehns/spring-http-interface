package com.kylehoehns.httpinterface;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.List;

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

  @Bean
  PostsClient postsClient(RestClient restClient) {
    return HttpServiceProxyFactory
        .builderFor(RestClientAdapter.create(restClient))
        .build()
        .createClient(PostsClient.class);
  }

}

interface PostsClient {

  @GetExchange("/posts/{id}")
  Post getPost(@PathVariable int id);

  @GetExchange("/posts")
  List<Post> getPosts();

  @DeleteExchange("/posts/{id}")
  void deletePost(@PathVariable int id);
}

record Post(int userId, int id, String title, String body) {
}

@RestController
@RequestMapping
class PostsRestController {

  private final PostsClient postsClient;

  public PostsRestController(PostsClient postsClient) {
    this.postsClient = postsClient;
  }

  @GetMapping("/posts/{id}")
  public Post getPost(@PathVariable int id) {
    return postsClient.getPost(id);
  }

  @GetMapping("/posts")
  public List<Post> getPosts() {
    return postsClient.getPosts();
  }

  @DeleteMapping("/posts/{id}")
  public void deletePost(@PathVariable int id) {
    postsClient.deletePost(id);
  }

}

