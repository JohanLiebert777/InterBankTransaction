# InterBankTransaction
This project is an example illustrating how to implement distributed transaction by using RocketMQ and Redis.
We employed the design pattern of reliable message and try-best-to-deliver message to fullfill the mechanism of transaction compensation.<br>
<br>
*Architecture: SpringBoot + RocketMQ + Redis (Cluster [Redisson Client API] ) + ShardingJDBC<br>
*Transaction Compensation: Reliable Message + Try-Best-To-Deliver Message<br>
*Database: Mysql<br>
<br>
To improve the throughput (QPS) of transfering money, we implemented the `Request Merger (utility)` by which we are able to queue the incoming requests. Whenever the size of buffer or the timeout is reached, the queued requests will be sent to service to proceed the business process.

*Throughput of Query: 250~270 (QPS)
*Throughput of Transfer Money: 50~60 (QPS)
