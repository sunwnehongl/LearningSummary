# Lock锁
  synchronized锁虽然简化了锁的获取和释放，但却缺少了一些扩展性，在一些特定的场景上没法实现，或者很难实现，比如多个线程共享式的访问共享资源、超时中断等。
### Lock锁和synchronized锁的对比
![Lock锁](https://github.com/sunwnehongl/LearningSummary/blob/master/image/concurrent/Lock%E9%94%81.png "Lock锁")
**synchronized**
**优点：**实现简单，语义清晰，方便JVM堆栈跟踪，加锁和解锁过程又JVM自动控制，而且JVM提高了各种锁优化的方案
**缺点：**悲观的排他锁，不能实现高级功能。
**Lock锁**
**优点：**可以中断、定时、尝试非阻塞式的获取锁，提高了读写锁、公平锁、非公平锁等高级功能。
**缺点：**需要显示的声明和释放锁
|   方法名| 描述  |
| ------------ | ------------ |
|  lock() |   |
|   |   |
|   |   |
|   |   |
|   |   |
|   |   |
|   |   |
