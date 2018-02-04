Do you need to run a process everyday at the exact same time like an alarm? Then Spring's scheduled tasks are for you. Allowing you to annotate a method with <code>@Scheduled</code> causing it to run at the specific time or interval that is denoted inside it. In this post we will look at setting up a project that can use scheduled tasks as well as how to use the different methods for defining when they execute.

I will be using Spring Boot for this post making the dependencies nice and simple due to scheduling being available to the <code>spring-boot-starter</code> dependency which will be included in pretty much every Spring Boot project in some way. This allows you to use any of the other starter dependencies as they will pull in <code>spring-boot-starter</code> and all its relationships. If you want to include the exact dependency itself, use <code>spring-context</code>.

You could use <code>spring-boot-starter</code>.

[gist https://gist.github.com/lankydan/28513a20cce085d8eab6fe2f424e5645 /]

Or use <code>spring-context</code> directly.

[gist https://gist.github.com/lankydan/7ca524c8c5a8f4d4c6052752c9af020f /]

Creating a scheduled task is pretty straight forward. Add the <code>@Scheduled</code> annotation to any method that you wish to run automatically and include <code>@EnableScheduling</code> in a configuration file.

So for example you could have something like the below.

[gist https://gist.github.com/lankydan/40363710bc5c0170ec6abedc22db242f /]

There is quite a lot of code here that has no importance to running a scheduled task. As I said a minute ago we need to use <code>@Scheduled</code> on a method and it will start running automatically. So in the above example the <code>create</code> method will start running every 1000ms (1 second) as denoted by the <code>fixedRate</code> property of the annotation. If we wanted to change how often it ran we could increase or decrease the <code>fixedRate</code> time or we could consider using the different scheduling methods available to us.

So you probably want to know what these other ways are right? Well here they are (I will include <code>fixedRate</code> here as well).
<ul>
	<li><code>fixedRate</code> executes the method with a fixed period of milliseconds between invocations.</li>
	<li><code>fixedRateString</code> the same as <code>fixedRate</code> but with a string value instead.</li>
	<li><code>fixedDelay</code> executes the method with a fixed period of milliseconds between the end of one invocation and the start of the next.</li>
	<li><code>fixedDelayString</code> the same as <code>fixedDelay</code> but with a string value instead.</li>
	<li><code>cron</code> uses cron-like expressions to determine when to execute the method (we will look at this more in depth later).</li>
</ul>
There are a few other utility properties available to the <code>@Scheduled</code> annotation.
<ul>
	<li><code>zone</code> Indicates the time zone that the cron expression will be resolved for, if no time zone is included it will use the server's default time zone. So if you needed it to run for a specific time zone, say Hong Kong, you could use <code>zone = "GMT+8:00"</code>.</li>
	<li><code>initialDelay</code> The number of milliseconds to delay the first execution of a scheduled task, requires one of the fixed rate or fixed delay properties to be used.</li>
	<li><code>initialDelayString</code> The same as <code>initialDelay</code> but with a string value instead.</li>
</ul>
A few examples of using fixed rates and delays can be found below.
<pre>@Scheduled(fixedRate = 1000)
</pre>
Same as earlier, runs every 1 second.
<pre>@Scheduled(fixedRateString = "1000")
</pre>
Same as above.
<pre>@Scheduled(fixedDelay = 1000)
</pre>
Runs 1 second after the previous invocation finished.
<pre>@Scheduled(fixedRate = 1000, initialDelay = 5000)
</pre>
Runs every second but waits 5 seconds before it executes for the first time.

Now onto looking at the <code>cron</code> property which gives much more control over the scheduling of a task, letting us define the seconds, minutes and hours the task runs at but can go even further and specify even the years that a task will run in.

Below is a breakdown of the components that build a cron expression.
<ul>
	<li><code>Seconds</code> can have values <code>0-59</code> or the special characters <code>, - * /</code> .</li>
	<li><code>Minutes</code> can have values <code>0-59</code> or the special characters <code>, - * /</code> .</li>
	<li><code>Hours</code> can have values <code>0-59</code> or the special characters <code>, - * /</code> .</li>
	<li><code>Day of month</code> can have values <code>1-31</code> or the special characters <code>, - * ? / L W C</code> .</li>
	<li><code>Month</code> can have values <code>1-12</code>, <code>JAN-DEC</code> or the special characters <code>, - * /</code> .</li>
	<li><code>Day of week</code> can have values <code>1-7</code>, <code>SUN-SAT</code> or the special characters <code>, - * ? / L C #</code> .</li>
	<li><code>Year</code> can be empty, have values <code>1970-2099</code> or the special characters <code>, - * /</code> .</li>
</ul>
Just for some extra clarity I have combined the breakdown into an expression consisting of the field labels.
<pre>@Scheduled(cron = "[Seconds] [Minutes] [Hours] [Day of month] [Month] [Day of week] [Year]")
</pre>
Please do not include the braces in your expressions (I used them to make the expression clearer).

Before we can on, we need to go through what the special characters mean.
<ul>
	<li><code>*</code> represents all values, so if used in the second field it means every second or used in the day field meaning run every day.</li>
	<li><code>?</code> represents no specific value and can be used in either the day of month or day of week field where using one invalidates the other. If we specify to trigger on the 15th day of a month then a <code>?</code> will be used in the <code>Day of week</code> field.</li>
	<li><code>-</code> represents a inclusive range of values, for example 1-3 in the hours field means the hours 1, 2 and 3.</li>
	<li><code>,</code> represents additional values, for example MON,WED,SUN in the day of week field means on Monday, Wednesday and Sunday.</li>
	<li><code>/</code> represents increments, for example 0/15 in the seconds field triggers every 15 seconds starting from 0 (0, 15, 30 and 45).</li>
	<li><code>L</code> represents the last day of the week or month. Remember that Saturday is the end of the week in this context, so using <code>L</code> in the day of week field will trigger on a Saturday. This can be used in conjunction with a number in the day of month field, such as <code>6L</code> to represent the last Friday of the month or an expression like <code>L-3</code> denoting the third from last day of the month. If we specify a value in the day of week field we must use <code>?</code> in the day of month field, and vise versa.</li>
	<li><code>W</code> represents the nearest weekday of the month. For example if <code>15W</code> will trigger on 15th day of the month if it is a weekday, otherwise it will run on the closest weekday. This value cannot be used in a list of day values.</li>
	<li><code>#</code> specifies both the day of the week and the week that the task should trigger. For example, <code>5#2</code> means the second Thursday of the month. If the day and week you specified overflows into the next month then it will not trigger.</li>
</ul>
A helpful resource with slightly longer explanations can be found <a href="http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/crontrigger.html" target="_blank" rel="noopener">here</a>, which helped me write this post.

Lets go through a few examples.
<pre>@Scheduled(cron = "0 0 12 * * ?")
</pre>
Fires at 12pm everyday.
<pre>@Scheduled(cron = "0 15 10 * * ? 2005")
</pre>
Fires at 10:15am everyday in the year 2005.
<pre>@Scheduled(cron = "0/20 * * * * ?")
</pre>
Fires every 20 seconds.

For some more examples see the link I mentioned earlier, shown again <a href="http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/crontrigger.html" target="_blank" rel="noopener">here</a>. Luckily, if you get stuck when writing a simple cron expression you should be able to google for the scenario that you need as someone has probably asked the same question on Stack Overflow already.

To tie some of the above into a little code example see the code below.

[gist https://gist.github.com/lankydan/2851c5c7a7fd352b4265ae1806eee969 /]

Here we have a class that is querying Cassandra every 20 seconds for the average value of events in the same time period. Again most of the code here is noise from the <code>@Scheduled</code> annotation but it can be helpful to see it in the wild. Furthermore if you have been observant, for this use-case of running every 20 seconds, using the <code>fixedRate</code> and possibly the <code>fixedDelay</code> properties instead of <code>cron</code> would be suitable here as we are running the task so frequently.
<pre>@Scheduled(fixedRate = 20000)
</pre>
Is the <code>fixedRate</code> equivalent of the cron expression used above.

The final requirement which I alluded to earlier is to add the <code>@EnableScheduling</code> annotation to a configuration class.

[gist https://gist.github.com/lankydan/c119b7d17457aaf025085fd9d6de8a19 /]

Being this is a small Spring Boot application I have attached the <code>@EnableScheduling</code> annotation to the main <code>@SpringBootApplication</code> class.

In conclusion, we can schedule tasks to trigger using the <code>@Scheduled</code> annotation along with either a millisecond rate between executions or a cron expression for finer timings that cannot be expressed with the former. For tasks that need to run very often, using the <code>fixedRate</code> or <code>fixedDelay</code> properties will suffice but once the time between executions becomes larger it will become harder to quickly determine the defined time. When this occurs the <code>cron</code> property should be used for better clarity of the scheduled timings.

The little amount of code used in this post can be found on my <a href="https://github.com/lankydan/spring-scheduled-tasks" target="_blank" rel="noopener">GitHub</a>.

If you found this post helpful and wish to keep up to date with my new tutorials as I write them, follow me on Twitter at <a href="https://twitter.com/LankyDanDev" target="_blank" rel="noopener">@LankyDanDev</a>.