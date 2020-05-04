# Lock锁
  synchronized锁虽然简化了锁的获取和释放，但却缺少了一些扩展性，在一些特定的场景上没法实现，或者很难实现，比如多个线程共享式的访问共享资源、超时中断等。
### Lock锁和synchronized锁的对比
![Lock锁](https://github.com/sunwnehongl/LearningSummary/blob/master/image/concurrent/Lock%E9%94%81.png "Lock锁")
**synchronized**
优点：实现简单，语义清晰，方便JVM堆栈跟踪，加锁和解锁过程又JVM自动控制，而且JVM提高了各种锁优化的方案。
缺点：悲观的排他锁，不能实现高级功能。
**Lock锁**
优点：可以中断、定时、尝试非阻塞式的获取锁，提高了读写锁、公平锁、非公平锁等高级功能。
缺点：需要显示的声明和释放锁
|   方法名| 描述  |
| ------------ | ------------ |
|void lock() |获取锁，调用该方法当前线程获取锁，直到获取锁该方法返回，否则一直阻塞。   |
|void lockInterruptibly()   | 可以响应中断的获取锁，如果在获取锁的过程中线程被中断则释放锁
并抛出异常。|
|boolean tryLock()   |尝试非阻塞式的获取锁，改方案调用后立即返回，如果获取到锁返回true，否则返回false。   |
|boolean tryLock(long time, TimeUnit unit)   |超时获取锁，改方法响应中断，如果在指定的时间内没有获取到锁则返回false。   |
|void unlock()   | 释放锁  |
|Condition newCondition()   | 获取等待通知组件，该组件和锁绑定，只有获取到锁后才能调用Condition的等待和唤醒方法。  |
###队列同步器AQS
[AQS同步器](https://github.com/sunwnehongl/LearningSummary/blob/master/java%E7%9F%A5%E8%AF%86%E6%80%BB%E7%BB%93/java%E5%B9%B6%E5%8F%91/AQS.md "AQS同步器")
###ReentrantLock
###ReentrantReadWriteLock
