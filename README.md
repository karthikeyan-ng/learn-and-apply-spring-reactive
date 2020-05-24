Refer classes in this order, so that learners will get more clear understanding

### Learn to write Junit Tests
Using Flux and Mono: `FluxAndMonoExampleTest`  
Flux / Mono - Exploring Factory methods: `FluxAndMonoFactoryTest`  
Filtering reactive streams `FluxAndMonoFilterTest`  
Transforming a Reactive Stream using map `FluxAndMonoTransformTest`  
Transforming a Reactive Stream using flatMap `FluxAndMonoCombineTest`  
Combining Reactive Streams `FluxAndMonoCombineTest`  
Handling Errors in a Reactive Streams `FluxAndMonoErrorTest`  
Infinite Reactive Streams `FluxAndMonoWithTimeTest`
Back Pressure on Reactive Data Streams - How it works? `FluxAndMonoBackPressureTest`  

Project Reactor Communication Model:  
    - Supports Both Pull and Pull model  
    - Pull: The Subscriber decides how to pull data from the Publisher  
    - Push: The Publisher decides how to push data to the Subscriber  

What is the Cold and Hot Reactive Stream? `ColdAndHotPublisherTest`  

Virtualizing Time in Junit - `VirtualTimeTest`

How to get help?
- Project Reactor reference guide
- Which Operator

### Application Rest Scenarios

Refer the classes in this order

#### Build the Non Blocking RESTFUL API using Annotated Controllers

* `FluxAndMonoController`  
* `FluxAndMonoControllerTest`  
    - @WebFluxTest: is Required on each test classes
    - purpose? it is going to scan for all the classes that are annotated with @RestController and @Controller and more.
    - This annotation will not scan @Component, @Service and @Repository
    
#### Build Non Blocking RESTFUL API using Functional Web

Spring WebFlux - Functional Web:  
* Use **Functions** to route the request and response.  
* **RouterFunction** and **HandlerFunction**  
* **RouterFunction**  
    * Use to route the incoming request  
    * Similar to the functionality of `@RequestMapping` annotation. Ex. `@GetMapping("/flux")`  
* **HandlerFunction**  
    * Handles the request and response  
    * Similar to the body of the `@RequestMapping` annotation. Ex. method body  
    * `ServerRequest` and `ServerResponse`  
    * **`ServerRequest`** represents the HttpRequest  
    * **`ServerResponse`** represents the HttpResponse  
    
* Refer the class  
    * `SampleHandlerFunction` and `RouterFunctionConfig`

```java
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class SampleHandlerFunction {

    public Mono<ServerResponse> flux(ServerRequest serverRequest) {

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        Flux.just(1, 2, 3, 4).log(), Integer.class
                );
    }

    public Mono<ServerResponse> mono(ServerRequest serverRequest) {

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        Mono.just(1).log(), Integer.class
                );
    }
}
```

Here `SampleHandlerFunction` which contains two methods `flux` and `mono`. Both methods receives `ServerRequest` object.

```java
import com.techstack.react.handler.SampleHandlerFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class RouterFunctionConfig {

    @Bean
    public RouterFunction<ServerResponse> route(SampleHandlerFunction sampleHandlerFunction) {
        return RouterFunctions
                .route(
                        GET("/functional/flux").and(accept(MediaType.APPLICATION_JSON)),
                        sampleHandlerFunction::flux)
                .andRoute(
                        GET("/functional/mono").and(accept(MediaType.APPLICATION_JSON)),
                        sampleHandlerFunction::mono);
    }
}
```

Inorder to execute `SampleHandlerFunction` methods `flux` and `mono` you have to create a Configuration class which contains a 
`@Bean` which takes input as your handler class `SampleHandlerFunction`.  
After that you can create a routes using `RouterFunctions` and add N number of routes using `route` and `andRoute` method.  
Each route configures HTTP method `GET`, `POST` with Rest path followed by configure the handler method.


