#AbstractQueuedSynchronizer队列同步器
    队列同步器AbstractQueuedSynchronizer简称AQS，用来构建锁和其他同步组件的基础，它通过一个int变量来表示状态，并用哪个一个内置的FIFO队列来完成线程获取资源的排队工作。
    同步器的底层实现了锁状态的管理、线程的排队、等待与唤醒等功能，如果想通过同步器实现锁或者其他同步组件，只需要实现
    同步器指定的抽象方法即可，同步器还为我们提供了一系列模板方法，根据我们的需要选择合适的方法来实现我们的功能

下面我们来一起看下看同步器可以重写和提供的模板方法。
![AQS可重写方法](https://github.com/sunwnehongl/LearningSummary/blob/master/image/concurrent/AQS%E5%8F%AF%E9%87%8D%E5%86%99%E6%96%B9%E6%B3%95.png "AQS可重写方法")