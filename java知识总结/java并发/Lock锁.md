# Lock锁
  synchronized锁虽然简化了锁的获取和释放，但却缺少了一些扩展性，在一些特定的场景上没法实现，或者很难实现，比如多个线程共享式的访问共享资源、超时中断等。
### Lock锁和synchronized锁的对比
![Lock锁](https://github.com/sunwnehongl/LearningSummary/blob/master/image/concurrent/Lock%E9%94%81.png "Lock锁")
