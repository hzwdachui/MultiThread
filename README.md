This is the cs108 assignment5
# MultiThread
Using thread technique to implement a cracker and a bank transaction manager. 

how to run: 
in the terminal 
in the dir "bin"
```
java edu.stanford.cs108/Bank ..\5k.txt 4
```

## Multithread homework
### bank 
- 封装了3个类，Account, Bank, Transaction
- Account 里同步：**写** 这个操作
- 主要是bank
- **BlockingQueue**是一种数据结构，支持一个线程往里存资源，另一个线程从里取资源。
	- 当队列中没有数据的情况下，消费者端的所有线程都会被自动阻塞（挂起），直到有数据放入队列
	- 当队列中填满数据的情况下，生产者端的所有线程都会被自动阻塞（挂起），直到队列中有空的位置，线程被自动唤醒
- 每个thread需要有latch.countdown()
- main里面latch.await()，把main线程停住了，main线程就等着其他的线程全部执行countdown()方法

### cracker
- 主要思路是把一个把一个破解工作交给好几个thread来破解，A破解开头是A~E的数字，B破解开头是F~J的数字
- 用的是暴力破解，一位一位的循环匹配
- 同样的先是main thread await()，在每个worker thread中的run()方法中用latch.countdown()，等到破解完成再运行main thread
