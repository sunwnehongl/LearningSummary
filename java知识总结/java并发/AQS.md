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
获取同步状态，该方法不影响线程中断，如果在获取锁的过程中，线程被其他线程中断，该方法不会响应，在进入同步队列后，如果线程被中断，当前节点不会被从同步队列中移除，该方法先调用重写的tryAcquire方法先尝试非阻塞的获取同步状态，如果获取成功则返回，或者把该线程加入到同步队列中，并循环获取同步传递或者挂起，如果被挂起，则等待头节点释放同步状态后唤醒。
```java
    public final void acquire(int arg) {
        if (!tryAcquire(arg) &&
                acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }
```
独占获取锁的流程图如下所示：
![独占获取锁的流程图](https://github.com/sunwnehongl/LearningSummary/blob/master/image/concurrent/%E7%8B%AC%E5%8D%A0%E7%9A%84%E8%8E%B7%E5%8F%96%E9%94%81.png)

把当前线程封装成节点后，添加到同步节点的尾部的代码如下，也就是方法addWaiter的代码逻辑。
```java
/**
     * 生成当前节点并把当前节点加入到同步队列的尾部。
     * @param mode 模式如果为SHARED则是共享模式，如果EXCLUSIVE则为独占模式
     * @return 当前节点
     */
    private Node addWaiter(Node mode) {
        // 生成当前节点。
        Node node = new Node(Thread.currentThread(), mode);
        // 先用pred记住当前时刻尾节点的引用，后面尾巴节点可能会发生变化。
        Node pred = tail;
        // 如果尾部节点不为空，则尝试快速的把节点添加同步队列到尾巴。
        if (pred != null) {
            // 先把当前节点的前驱节点指向尾部节点，
            node.prev = pred;
            /* 并用CAS把当前节点设置成尾巴节点，如果CAS失败说明在中间有其他线程
             * 被添加到了同步队列的尾部，需要继续执行enq方法，把该节点添加到尾部。
             */
            if (compareAndSetTail(pred, node)) {
                // 如果当前节点被设置成尾节点后，后续的节点只会添加到当前节点的后面,
                // 直接把原来的尾部节点的后继节点指向当前节点就完成了节点加入同步队列。
                pred.next = node;
                return node;
            }
        }
        // 调用enq方法循环的参数把节点添加到尾部节点，直到成功后返回。
        enq(node);
        return node;
    }
```
把当前节点加入到同步队列的方法enq的代码逻辑如下：
```java
    /**
     * 把节点添加到同步队列中
     */
    private Node enq(final Node node) {
        // 循环的尝试通过CAS把节点添加到同步队列中，
        for (;;) {
            // 用个对象先记着尾部节点的引用，方便后面CAS方法用。
            Node t = tail;
            /**
             * 如果尾部节点为空，则通过CAS把头节点设置成一个空节点，
             * 空节点表示没节点获取同步状态，如果设置成功后，还没用设置尾节点
             * 这断时间tail还是为空，所以  tail = head不用CAS，
             */
            if (t == null) {
                if (compareAndSetHead(new Node()))
                    tail = head;
            } else {
                // 此代码和addWaiter方法快速加入到同步队列的代码相同
                node.prev = t;
                if (compareAndSetTail(t, node)) {
                    t.next = node;
                    return t;
                }
            }
        }
    }
```

```java
    /**
     * 在循环里不断的获取同步状态，
     * 如果前驱节点是头节点，则不一致不断的获取同步状态，如果不是同步状态就先判断
     * 是否需要暂停当前线程，如果需要暂停就暂定当前线程。等头节点释放同步状态后再唤醒当前
     * 线程。
     */
    final boolean acquireQueued(final Node node, int arg) {
        boolean failed = true;
        try {
            boolean interrupted = false;
            for (;;) {
                // 如果头节点是前驱节点就尝试非阻塞的获取同步状态
                final Node p = node.predecessor();
                if (p == head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return interrupted;
                }
                // 通过判断前面节点的状态判断当前节点是否需要暂停，如果需要就暂停当前线程
                if (shouldParkAfterFailedAcquire(p, node) &&
                        parkAndCheckInterrupt())
                    interrupted = true;
            }
        } finally {
            // 如果当前节点获取同步状态失败，则从同步队列中删除当前节点。
            if (failed)
                cancelAcquire(node);
        }
    }
```
```java
    /**
     * 判断当节点是否需要暂停当前线程。
     */
    private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
        int ws = pred.waitStatus;
        if (ws == Node.SIGNAL)
            /*
             * 如果前驱节点的状态为SIGNAL，表示如果前驱节点获取到同步状态或者被取消后会唤醒后面的节点
             * 这种情况可以暂停当前线程
             */
            return true;
        if (ws > 0) {
            /*
             * 如果前驱节点的状态为CANCELLED，则一直向前找，直到找到状态不为CANCELLED的节点为止
             * 并把当前节点的前驱节点设置为此节点，并把此节点的后继节点设置为当前节点。然后返回false。
             */
            do {
                node.prev = pred = pred.prev;
            } while (pred.waitStatus > 0);
            pred.next = node;
        } else {
            /*
             *  如果前驱节点的状态为初始状态或者PROPAGATE，则说明我们需要一个SIGNAL
             *  不能挂起当前线程，我们需要把前驱节点的状态设置为SIGNAL。
             */
            compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
        }
        return false;
    }
```
取消节点主要分为如下几种情况：
1. 如果取消的节点为null，则不处理直接返回。
2. 如果取消的节点为尾节点，则把此节点前面的节点（状态不会取消的节点）设置为尾节点。
3. 如果当前节不尾节点，且前面节点也不是头节点，则利用CAS把前驱节点的next设置为当前节点的next。
4. 如果前驱节点为头节点，则把当前节点的后继节点唤醒即可。

```java
 /**
     * 获取同步状态失败后，取消当前节点
     */
    private void cancelAcquire(Node node) {
        // 如果节点为空，不用处理直接返回
        if (node == null)
            return;
        // 把当前节点的线程设置为空
        node.thread = null;

        // 找到当前节点之前没有被取消的节点，并把中间已经取消的节点，在同步队列中删除
        Node pred = node.prev;
        while (pred.waitStatus > 0)
            node.prev = pred = pred.prev;

        Node predNext = pred.next;

        // 把当前节点的状态设置为取消
        node.waitStatus = Node.CANCELLED;

        // 如果当前节点是尾节点,节把当前节点的前驱节点设置为尾节点，也就是在同步队列中删除当前节点
        if (node == tail && compareAndSetTail(node, pred)) {
            // 尾节点设置成功后，把新的为节点的后继节点设置为null
            compareAndSetNext(pred, predNext, null);
        } else {
            /**
             * 前驱节点也不是头节点，并且前驱节点的状态为SIGNAL，就利用CAS把当前的next设置为
             * 前驱节点的next；
             */

            int ws;
            if (pred != head &&
                    ((ws = pred.waitStatus) == Node.SIGNAL ||
                            (ws <= 0 && compareAndSetWaitStatus(pred, ws, Node.SIGNAL))) &&
                    pred.thread != null) {
                Node next = node.next;
                if (next != null && next.waitStatus <= 0)
                    compareAndSetNext(pred, predNext, next);
            } else {
                // 如果当前节点的前驱节点为头节点，就帮唤醒当前节点的后继节点
                unparkSuccessor(node);
            }
            node.next = node; // help GC
        }
    }
```

**同步状态的释放**
```java
 /**
     * 释放同步状态，因为独占的模式下不存在同事释放同步状态，
     * 不存在竞争，不用CAS就可以保证原子性。
     */
    public final boolean release(int arg) {
        // 非阻塞的释放同步状态
        if (tryRelease(arg)) {
            Node h = head;
            // 释放同步状态后，如果头节点的转态不是为零，则调用unparkSuccessor方法
            // 唤醒后继被暂停的节点
            if (h != null && h.waitStatus != 0)
                unparkSuccessor(h);
            return true;
        }
        return false;
    }
```
### 共享的获取和释放同步状态
### 限时获取同步状态