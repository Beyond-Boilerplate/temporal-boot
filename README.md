Here’s a **checklist** of **DOs and DON'Ts** when working with Temporal in a Spring Boot application, specifically focusing on code practices to follow:

### **DOs**

#### 1. **DO Use Temporal's Workflow Time API**
   - **Use `Workflow.currentTimeMillis()` or `Workflow.currentDateTime()`** instead of `System.currentTimeMillis()` or `LocalDateTime.now()`. This ensures deterministic behavior in workflows.
   
   ```java
   long currentTime = Workflow.currentTimeMillis();
   ```

#### 2. **DO Keep Workflow Code Deterministic**
   - Ensure that the code inside workflows is deterministic (i.e., it should produce the same results on re-execution).
   - **Avoid non-deterministic operations like**:
     - Current system time (`System.currentTimeMillis()`).
     - Random numbers (`Math.random()`).
     - System calls, file I/O, and database calls inside workflows.

#### 3. **DO Encapsulate Non-Deterministic Code in Activities**
   - Put any non-deterministic code, such as HTTP requests, database calls, or system-dependent actions (e.g., reading files), inside activities rather than workflows.

   ```java
   @ActivityMethod
   public String fetchFromAPI(String url);
   ```

#### 4. **DO Define Activities and Workflows as Interfaces**
   - Always define workflows and activities as **interfaces** and provide their implementation separately.

   ```java
   public interface MyWorkflow {
       @WorkflowMethod
       String process(String input);
   }

   public interface MyActivities {
       @ActivityMethod
       String fetchData(String input);
   }
   ```

#### 5. **DO Use Retry Policies**
   - Define retry policies for activities to handle failures gracefully. Use Temporal’s built-in retry mechanism for activities.

   ```java
   RetryOptions retryOptions = RetryOptions.newBuilder()
           .setInitialInterval(Duration.ofSeconds(1))
           .setMaximumAttempts(5)
           .build();
   
   ActivityOptions options = ActivityOptions.newBuilder()
           .setRetryOptions(retryOptions)
           .build();
   ```

#### 6. **DO Use Spring Beans in Activities**
   - Since activities are normal Java classes, they can take advantage of Spring Boot features like dependency injection.
   
   ```java
   @Service
   public class MyActivitiesImpl implements MyActivities {
       @Autowired
       private MyService myService;
       
       @Override
       public String fetchData(String input) {
           return myService.process(input);
       }
   }
   ```

#### 7. **DO Handle Workflow Versioning**
   - Use versioning when making backward-incompatible changes to workflows to ensure that workflows already in progress continue functioning.

   ```java
   int version = Workflow.getVersion("myChange", Workflow.DEFAULT_VERSION, 2);
   if (version == Workflow.DEFAULT_VERSION) {
       // Old behavior
   } else {
       // New behavior
   }
   ```

#### 8. **DO Use Signals and Queries for Communication**
   - **Signals** can be used to send external input into running workflows asynchronously.
   - **Queries** can be used to fetch the current state of a workflow without modifying its execution.

   ```java
   @SignalMethod
   void notifyOfEvent(String eventName);
   
   @QueryMethod
   String getCurrentStatus();
   ```

#### 9. **DO Use Workflow Futures and Promises for Async Operations**
   - Temporal supports asynchronous operations within workflows using **Futures** and **Promises**. Utilize these for parallel execution of activities.

   ```java
   Promise<String> future = Async.function(myActivities::fetchData, input);
   String result = future.get();
   ```

#### 10. **DO Use `Workflow.newActivityStub()` to Create Activity Stubs**
   - Always use `Workflow.newActivityStub()` to create activity stubs inside workflows. This ensures Temporal can track and manage the activity’s lifecycle.

   ```java
   MyActivities activities = Workflow.newActivityStub(MyActivities.class, options);
   ```

#### 11. **DO Leverage Spring Boot Configuration for Temporal Settings**
   - Use Spring Boot configuration to manage properties such as Temporal service URLs, namespaces, and worker configurations through `application.properties` or `application.yml`.

   ```yaml
   temporal:
     namespace: "default"
     service-url: "localhost:7233"
   ```

---

### **DON'Ts**

#### 1. **DON’T Use Non-Deterministic APIs in Workflows**
   - Avoid using APIs like `System.currentTimeMillis()`, `Math.random()`, `Thread.sleep()`, or any form of system interaction in workflow code.
   - These will break Temporal’s replay mechanism and lead to unexpected behavior.

#### 2. **DON’T Perform Blocking Calls in Workflows**
   - Never use blocking calls like `Thread.sleep()` or I/O operations in workflows. Use Temporal’s built-in timers instead.

   ```java
   Workflow.sleep(Duration.ofMinutes(10));
   ```

#### 3. **DON’T Call Activities Directly in the Workflow Implementation**
   - Never directly call activity implementations inside workflows. Always invoke activities via their stubs. This allows Temporal to handle retries, failures, and task distribution.

   ```java
   // Incorrect
   MyActivitiesImpl activities = new MyActivitiesImpl();
   activities.fetchData(input);
   
   // Correct
   MyActivities activities = Workflow.newActivityStub(MyActivities.class, options);
   ```

#### 4. **DON’T Modify Global Variables or States in Workflows**
   - Avoid modifying global or shared states within workflows, as this can introduce non-deterministic behavior.
   
   ```java
   // Avoid modifying global/static variables
   static int counter = 0; 
   
   @WorkflowMethod
   public void execute() {
       counter++;  // This introduces non-determinism
   }
   ```

#### 5. **DON’T Use External Libraries that Depend on System Time or Threading**
   - Avoid using libraries that rely on system time, multi-threading, or other non-deterministic operations. These can break the deterministic nature of workflows.

#### 6. **DON’T Assume Activity and Workflow Code Will Run on the Same Machine**
   - Temporal workflows and activities may run on different machines. Avoid any direct resource sharing or assuming a shared environment between them.

#### 7. **DON’T Use Exception Handling to Control Workflow Flow**
   - Avoid using exceptions for normal workflow logic flow control. Use Temporal’s retry and failure mechanisms instead to handle errors properly.

#### 8. **DON’T Overuse Workflow Sleep**
   - Avoid excessive use of `Workflow.sleep()` for polling or waiting. Instead, consider using activities for waiting on external conditions or events, or leverage **signals**.

#### 9. **DON’T Hardcode Workflow and Activity Configurations**
   - Avoid hardcoding activity timeout or retry policies in the workflow. Use Spring configuration files or pass them through constructors to make your workflows flexible.

   ```yaml
   temporal:
     activities:
       default-retry:
         initial-interval: 1s
         max-attempts: 3
   ```

#### 10. **DON’T Forget to Test Workflows and Activities Separately**
   - Always test workflows and activities in isolation. Temporal provides testing frameworks like `TestWorkflowEnvironment` that let you simulate workflow execution without depending on the actual Temporal service.

---

### **Conclusion:**
Following these **DOs and DON'Ts** ensures that your Temporal workflows and activities remain deterministic, scalable, and maintainable within a **Spring Boot** environment. By leveraging Temporal’s features and best practices, you can build highly reliable distributed systems that are easier to manage and debug.