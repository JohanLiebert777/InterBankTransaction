# InterBankTransaction
This project is an example illustrating how to implement distributed transaction.
We employed the design pattern of reliable message and try-best-to-deliver message to fullfill the mechanism of transaction compensation.<br>
<br>
*Architecture: SpringBoot + RocketMQ + Redis (Cluster [Redisson Client API] ) + ShardingJDBC<br>
*Transaction Compensation: Reliable Message + Try-Best-To-Deliver Message<br>
*StreamLimiter: Bucket Token Algorithm (provided by RateLimitor of Google Guava)<br>
*Database: Mysql<br>
<br>
To improve the throughput (QPS) of transfering money, we implemented the `Request Merger (utility)` by which we are able to queue the incoming requests. Whenever the size of buffer or the timeout is reached, the queued requests will be sent to service to proceed the business process.<br>
Request Merger is using the typical way of thinking in the area of computer science when solving problems. It is to trade space for time.<br>
<br>
*Throughput of Query: 250-270 (QPS)<br>
*Throughput of Transfer Money (without Request Merger): 10-15 (QPS)<br>
*Throughput of Transfer Money (with Request Merger): 50-60 (QPS)<br>
(Using Jmeter, fired 1000 threads)<br>
<br>
*Redis becomes the bottelneck of throughput. Our Redis Cluster can only afford 250-270 QPS.<br>
