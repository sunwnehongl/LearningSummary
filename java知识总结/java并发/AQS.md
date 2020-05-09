# AbstractQueuedSynchronizer队列同步器
   队列同步器AbstractQueuedSynchronizer简称AQS，用来构建锁和其他同步组件的基础，它通过一个int变量来表示状态，并用哪个一个内置的FIFO队列来完成线程获取资源的排队工作。
   同步器的底层实现了锁状态的管理、线程的排队、等待与唤醒等功能，如果想通过同步器实现锁或者其他同步组件，只需要实现
   同步器指定的抽象方法即可，同步器还为我们提供了一系列模板方法，根据我们的需要选择合适的方法来实现我们的功能

下面我们来一起看下看同步器可以重写和提供的模板方法。
### ASQ可以重写的方法
![AQS可重写方法](https://github.com/sunwnehongl/LearningSummary/blob/master/image/concurrent/AQS%E5%8F%AF%E9%87%8D%E5%86%99%E6%96%B9%E6%B3%95.png "AQS可重写方法")
### AQS的模板方法
| 模板方法  | 描述  |
| ------------ | ------------ |
|void acquire(int arg)  | 独占的获取同步状态，如果当前线程获取同步状态成功则返回，否则该线程进入同步队列等待。  |
|boolean release(int arg)  |独占的释放同步状态，释放当前线程占有的同步状态，并且唤醒后继节点线程。   |
|void acquireInterruptibly(int arg)  | 与acquire方法相同，只是改方法在获取同步状态时能够响应中断，如果在获取同步状态的过程中当前线程被中断，则抛出异常。  |
|boolean tryAcquireNanos(int arg, long nanosTimeout) |出了能够响应中断，还可以指定超时限制，如果在指定时间内没有获取状态则停止阻塞并返回false，如果成功获取到同步状态则返回true。   |
|void acquireShared(int arg)   |共享的获取同步状态，如果当前线程获取同步状态成功这返回，否则进入同步队列并阻塞，和acquire不同的是，该方法支持多个线程同时获取同步状态。	   |
|void acquireSharedInterruptibly(int arg)   |在acquireShared方法的基础上增加了响应中断。   |
|boolean tryAcquireSharedNanos(int arg, long nanosTimeout)   | 在acquireSharedInterruptibly方法的基础上增加了超时限制。  |
|boolean releaseShared(int arg)   | 共享的释放同步状态。  |
### 同步队列
![同步队列](https://github.com/sunwnehongl/LearningSummary/blob/master/image/concurrent/%E5%90%8C%E6%AD%A5%E9%98%9F%E5%88%97.png)
AQS的同步队列是一个双向队列，AQS有个head属性指向队列的头节点，有个trail属性指向队列的尾节点，每个节点都保存了前驱节点和后驱节点的引用，每个节点的定义如下。

```java
    static final class Node {
        /** 共享模式每个节点waitStatus的值  */
        static final Node SHARED = new Node();
        /** 独占模式下每个节点waitStatus的值 */
        Node EXCLUSIVE = null;

        static final int CANCELLED =  1;
        static final int SIGNAL    = -1;
        static final int CONDITION = -2;
        static final int PROPAGATE = -3;

        /**
         * 等待状态
         *   SIGNAL: 表示当前节点的同步状态被释放或者取消，那么将会唤醒后面没有被取消的挂起节点
         *   CANCELLED: 表示当前等待节点节点的线程被中断，或者当前等待节点等待超出了时间限制，
         *   因而取消了等待。
         *   CONDITION: 表示当前节点在等待队列中，等待在Condition上，只有Condition调用了
         *   signal方法后，该节点才会从等待队列中转移到同步队列。
         *   PROPAGATE: 表示共享模式下次将会无条件的传播下去，
         */
        volatile int waitStatus;

        /**
         * 当前节点的前驱节点,当前线程依赖它来检查waitStatus,在入队的时候才被分配,
         * 并且只在出队的时候才被取消(为了GC),头节点永远不会被取消,一个节点成为头节点
         * 仅仅是成功获取到锁的结果,一个被取消的线程永远也不会获取到锁,线程只取消自身,
         * 而不涉及其他节点
         */
        volatile Node prev;

        /**
         * 当前节点的后继节点,当前线程释放的才被唤起,在入队时分配,在绕过被取消的前驱节点
         * 时调整,在出队列的时候取消(为了GC)
         * 如果一个节点的next为空,我们可以从尾部扫描它的prev,双重检查
         * 被取消节点的next设置为指向节点本身而不是null,为了isOnSyncQueue更容易操作
         */
        volatile Node next;

        /**
         * 当前节点的线程
         */
        volatile Thread thread;

        /**
         * 等待队列中的后继节点，如果当前节点是共享的，那么这个这个字段就是一个常量为SHARED，如果是独占的则和next的值一样。
         */
        Node nextWaiter;

        /**
         * 如果是共享模式则返回true
         */
        final boolean isShared() {
            return nextWaiter == SHARED;
        }

        /**
         * 返回当前节点的前驱节点,如果为空,直接抛出空指针异常
         */
        final Node predecessor() throws NullPointerException {
            Node p = prev;
            if (p == null)
                throw new NullPointerException();
            else
                return p;
        }

        Node() {
        }

        Node(Thread thread, Node mode) {
            this.nextWaiter = mode;
            this.thread = thread;
        }

        Node(Thread thread, int waitStatus) {
            this.waitStatus = waitStatus;
            this.thread = thread;
        }
    }
```

| 属性名称  | 属性含义  |
| ------------ | ------------ |
| waitStatus  |  等待状态 |
| prev  | 前驱节点  |
| next  | 后驱节点  |
| thread  | 当前节点的线程,初始化后使用,在使用后失效  |
| nextWaiter  | 等待队列中的后继节点，如果当前节点是共享的，那么这个这个字段就是一个常量为SHARED，如果是独占的则和next的值一样。	  |


### 独占的获取和释放同步状态
### 共享的获取和释放同步状态
### 限时获取同步状态