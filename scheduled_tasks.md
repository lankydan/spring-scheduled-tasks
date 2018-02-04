Do you need to run a process everyday at the exact same time like an alarm? Then Spring's scheduled tasks are for you. Allowing you to annotate a method with `@Scheduled` causing it to run at the specific time or interval that is denoted inside it. In this post we will look at setting up a project that can use scheduled tasks as well as how to use the different methods for defining when they execute.

I will be using Spring Boot for this post making the dependencies nice and simple due to scheduling being available to the `spring-boot-starter` dependency which will be included in pretty much every Spring Boot project in some way. This allows you to use any of the other starter dependencies as they will pull in `spring-boot-starter` and all its relationships. If you want to include the exact dependency itself, use `spring-context`.

You could use `spring-boot-starter`.
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter</artifactId>
  <version>2.0.0.RC1</version>
</dependency>
```
Or use `spring-context` directly.
```xml
<dependency>
  <groupId>org.springframework</groupId>
  <artifactId>spring-context</artifactId>
  <version>5.0.3.RELEASE</version>
</dependency>
```
Creating a scheduled task is pretty straight forward. Add the `@Scheduled` annotation to any method that you wish to run automatically and include `@EnableScheduling` in a configuration file. 

So for example you could have something like the below.
```java
@Component
public class EventCreator {

  private static final Logger LOG = LoggerFactory.getLogger(EventCreator.class);

  private final EventRepository eventRepository;

  public EventCreator(final EventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  @Scheduled(fixedRate = 1000)
  public void create() {
    final LocalDateTime start = LocalDateTime.now();
    eventRepository.save(
        new Event(new EventKey("An event type", start, UUID.randomUUID()), Math.random() * 1000));
    LOG.debug("Event created!");
  }
}
```
There is quite a lot of code here that has no importance to running a scheduled task. As I said a minute ago we need to use `@Scheduled` on a method and it will start running automatically. So in the above example the `create` method will start running every 1000ms (1 second) as denoted by the `fixedRate` property of the annotation. If we wanted to change how often it ran we could increase or decrease the `fixedRate` time or we could consider using the different scheduling methods available to us.

So you probably want to know what these other ways are right? Well here they are (I will include `fixedRate` here as well).
- `fixedRate` Executes the method with a fixed period of milliseconds between invocations.
- `fixedRateString` The same as `fixedRate` but with a string value instead.
- `fixedDelay` Executes the method with a fixed period of milliseconds between the end of one invocation and the start of the next.
- `fixedDelayString` The same as `fixedDelay` but with a string value instead.
- `cron` Uses cron-like expressions to determine when to execute the method (we will look at this more in depth later).

There are a few other utility properties available to the `@Scheduled` annotation. 
- `zone` Indicates the time zone that the cron expression will be resolved for, if no time zone is included it will use the server's default time zone. So if you needed it to run for a specific time zone, say Hong Kong, you could use `zone = "GMT+8:00"`.
- `initialDelay` The number of milliseconds to delay the first execution of a scheduled task, requires one of the fixed rate or fixed delay properties to be used.
- `initialDelayString` The same as `initialDelay` but with a string value instead.

A few examples of using fixed rates and delays can be found below.
```java
@Scheduled(fixedRate = 1000)
```
Same as earlier, runs every 1 second.
```java
@Scheduled(fixedRateString = "1000")
```
Same as above.
```java
@Scheduled(fixedDelay = 1000)
```
Runs 1 second after the previous invocation finished.
```java
@Scheduled(fixedRate = 1000, initialDelay = 5000)
```
Runs every second but waits 5 seconds before it executes for the first time.

Now onto looking at the `cron` property which gives much more control over the scheduling of a task, letting us define the seconds, minutes and hours the task runs at but can go even further and specify even the years that a task will run in.

Below is a breakdown of the components that build a cron expression.
- `Seconds` can have values `0-59` or the special characters `, - * /` .
- `Minutes` can have values `0-59` or the special characters `, - * /` .
- `Hours` can have values `0-59` or the special characters `, - * /` .
- `Day of month` can have values `1-31` or the special characters `, - * ? / L W C` .
- `Month` can have values `1-12`, `JAN-DEC` or the special characters `, - * /` .
- `Day of week` can have values `1-7`, `SUN-SAT` or the special characters `, - * ? / L C #` .
- `Year` can be empty, have values `1970-2099` or the special characters `, - * /` .

Just for some extra clarity I have combined the breakdown into an expression consisting of the field labels.
```java
@Scheduled(cron = "<Seconds> <Minutes> <Hours> <Day of month> <Month> <Day of week> <Year>")
```
Please do not include the braces in your expressions (I used them to make the expression clearer).

Before we can on, we need to go through what the special characters mean.
- `*` represents all values, so if used in the second field it means every second or used in the day field meaning run every day.
- `?` represents no specific value and can be used in either the day of month or day of week field where using one invalidates the other. If we specify to trigger on the 15th day of a month then a `?` will be used in the `Day of week` field.
- `-` represents a inclusive range of values, for example 1-3 in the hours field means the hours 1, 2 and 3.
- `,` represents additional values, for example MON,WED,SUN in the day of week field means on Monday, Wednesday and Sunday.
- `/` represents increments, for example 0/15 in the seconds field triggers every 15 seconds starting from 0 (0, 15, 30 and 45).
- `L` represents the last day of the week or month. Remember that Saturday is the end of the week in this context, so using `L` in the day of week field will trigger on a Saturday. This can be used in conjunction with a number in the day of month field, such as `6L` to represent the last Friday of the month or an expression like `L-3` denoting the third from last day of the month. If we specify a value in the day of week field we must use `?` in the day of month field, and vise versa.
- `W` represents the nearest weekday of the month. For example if `15W` will trigger on 15th day of the month if it is a weekday, otherwise it will run on the closest weekday. This value cannot be used in a list of day values.
`#` specifies both the day of the week and the week that the task should trigger. For example, `5#2` means the second Thursday of the month. If the day and week you specified overflows into the next month then it will not trigger. 

A helpful resource with slightly longer explanations can be found [here](http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/crontrigger.html), which helped me write this post.

Lets go through a few examples.
```java
@Scheduled(cron = "0 0 12 * * ?")
```
Fires at 12pm everyday.
```java
@Scheduled(cron = "0 15 10 * * ? 2005")
```
Fires at 10:15am everyday in the year 2005.
```java
@Scheduled(cron = "0/20 * * * * ?")
```
Fires every 20 seconds.

For some more examples see the link I mentioned earlier, shown again [here](http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/crontrigger.html). Luckily, if you get stuck when writing a simple cron expression you should be able to google for the scenario that you need as someone has probably asked the same question on Stack Overflow already.

To tie some of the above into a little code example see the code below.
```java
@Component
public class AverageMonitor {

  private static final Logger LOG = LoggerFactory.getLogger(AverageMonitor.class);
  private final EventRepository eventRepository;
  private final AverageRepository averageRepository;

  public AverageMonitor(
      final EventRepository eventRepository, final AverageRepository averageRepository) {
    this.eventRepository = eventRepository;
    this.averageRepository = averageRepository;
  }

  @Scheduled(cron = "0/20 * * * * ?")
  public void publish() {
    final double average =
        eventRepository.getAverageValueGreaterThanStartTime(
            "An event type", LocalDateTime.now().minusSeconds(20));
    averageRepository.save(
        new Average(new AverageKey("An event type", LocalDateTime.now()), average));
    LOG.info("Average value is {}", average);
  }
}
```
Here we have a class that is querying Cassandra every 20 seconds for the average value of events in the same time period. Again most of the code here is noise from the `@Scheduled` annotation but it can be helpful to see it in the wild. Furthermore if you have been observant, for this use-case of running every 20 seconds, using the `fixedRate` and possibly the `fixedDelay` properties instead of `cron` would be suitable here as we are running the task so frequently.
```java
@Scheduled(fixedRate = 20000)
```
Is the `fixedRate` equivalent of the cron expression used above.

The final requirement which I alluded to earlier is to add the `@EnableScheduling` annotation to a configuration class.
```java
@SpringBootApplication
@EnableScheduling
public class Application {

  public static void main(final String args[]) {
    SpringApplication.run(Application.class);
  }
}
```
Being this is a small Spring Boot application I have attached the `@EnableScheduling` annotation to the main `@SpringBootApplication` class.

In conclusion, we can schedule tasks to trigger using the `@Scheduled` annotation along with either a millisecond rate between executions or a cron expression for finer timings that cannot be expressed with the former. For tasks that need to run very often, using the `fixedRate` or `fixedDelay` properties will suffice but once the time between executions becomes larger it will become harder to quickly determine the defined time. When this occurs the `cron` property should be used for better clarity of the scheduled timings.

The little amount of code used in this post can be found on my [GitHub](https://github.com/lankydan/spring-scheduled-tasks).

If you found this post helpful and wish to keep up to date with my new tutorials as I write them, follow me on twitter at [@LankyDanDev](https://twitter.com/LankyDanDev).