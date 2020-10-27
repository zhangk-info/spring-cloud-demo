# JVM

* ClassFiles -> ClassLoader 类装载器
* Runtime Data Area 运行时数据区
    * Method Area 方法区
    * Heap 堆
    * Java stack java栈
    * Native Method Stack 本地方法栈
    * Program Counter Register 程序计数器
* Execution Engine 执行引擎
* Native Interface 本地方法接口
* 本地方法库


### ClassLoader:
    负责加载class文件。
    class文件在文件开头有特定的文件标示。
    ClassLoader将class文件字节码内容加载到内存中，并将这些内容转换方法区中的运行时数据结构。
    ClassLoader只负责class文件的加载，至于它是否可以运行，则由Execution Engine决定。
##### 虚拟机自带的加载器 ClassLoader
* 启动类加载器 Bootstrap C++ /jre/lib/rt.jar
* 扩展类加载器 Extension/Ext Java /jre/lib/ext/*.jar
* 应用程序类加载器 App Java也叫 系统类加载器，加载当前应用的classpath的所有类
##### 用户自定义加载器 
#### sun.misc.Launcher 是java虚拟机的入口 
#### 双亲委派机制 是类的加载机制的重要体现
* 自顶向下加载： 从最上层开始找 bootstrap -> extension -> app 
* 沙箱安全机制:  保证java源代码不受污染，保证了使用不同的类加载器最终得到的都是同一个对象（包名类名相同的一定是同一个对象），不会拿到自定义的 java.lang.String

### Execution Engine：
    执行引擎负责解释命令，提交操作系统执行。
    
### Native:
    Native标识的本地方法调用Navtive Interface（本地方法接口）调用Native Method Stack（本地方法栈）调底层第三方（C语言）的函数库（不同操作系统函数库不同）

### Program Counter Register PC寄存器/程序寄存器 （指针/排班表）
    每个线程都有一个程序计数器，是线程私有的，就是一个指针。
    指向方法区中的方法字节码（用来存储指向下一条指令的地址，也即将要执行的指令代码），由执行引擎读取下一条指令，是一个非常小的内存空间，忽略不计。
    这块内存区域很小，它是当前线程所执行的字节码的行号指示器，字节码解释器通过改变这个计数器的值来选取下一条需要执行的字节码指令。
    如果执行的是一个native方法，那这个计数器是空的。
    用以完成分支、循环、跳转、异常处理、线程恢复等基础功能。不会发生内存溢出（OOM）错误。

### Method Area
    方法区是供各线程共享的运行时内存区域，存储的是类的结构信息（模板），例如：运行时常量池（Runtime Constant Pool）、字段和方法数据、构造函数和普通方法的字节码内容。
    上面讲的是规范，在不同虚拟机里头实现是不一样的，最典型的就是永久代(PermGen Space)和元空间(Metaspace)
    实例变量存在堆内存中，和方法区无关！
    
## Heap & Stack
    栈管运行 堆管存储
### Stack
    栈也叫栈内存，主管Java程序的运行，是在线程创建时创建，它的生命周期是跟随线程的生命周期，线程结束栈内存也就释放。
    对于栈来说不存在垃圾回收问题，只要线程一结束栈就结束，是线程私有的。
    8中基本数据类型+对象的引用变量+实例方法都是在函数的栈内存中分配。
* 栈存储什么：
    栈帧（java方法在虚拟机中叫栈帧,一个方法叫一个栈帧）中主要保存3类数据：本地变量（输入输出参数及方法中的变量）；栈操作（记录出栈入栈的操作）；栈帧数据（包括类文件、方法等）。
* java.lang.StackOverflowError sof 栈压满了     
### Heap    
    新生代（伊甸区（Eden Space）、幸存0区（S0/from）、幸存1区（S1/to））、养老区、元空间（java7永久代）
* 交换：from区和to区，他们的位置和名分不是固定的，每次GC后会交换，谁空谁是to
* 物理上堆大小比例： Yong:Old = 1:2; Eden:From:To = 8:1:1 （物理上没有元空间，逻辑上才有元空间）
* 新生区MinorGC过程： 复制->清空->互换 
    * 复制： Eden和From中活着的复制到To,年龄+1
    * 清空：每次GC会清空Eden和From
    * 交换：From变To
    * 15岁养老
* 养老区MajorGC/FullGC
* 方法区（Method Area）和堆一样，是各个线程共享的内存区域，它用于存储虚拟机加载的：类信息、普通常量、静态变量、编译器编译后的代码等等。
  虽然JVM规范将方法区描述为堆得一个逻辑部分，但它却还有一个别名叫做Non-Heap(非堆)，目的就是要和堆分开。
* Java8中，元空间并不在虚拟机中而是使用本机物理内存。


## JMM：Java内存模型 
    JMM中规定所有变量都存储在主内存，主内存是共享内存区域，所有线程都可访问。
    但线程对变量的操作（读取赋值等）必须在工作内存中进行，首先要将变量复制到工作内存，然后操作，操作完成再写回主内存中。
    不能直接操作主内存中的变量，各个线程中的工作内存中存储着主内存中的变量副本拷贝。
    因此不同线程间无法访问对方的工作内存，线程间的通信（传值）必须通过主内存来完成。
* volatile是java虚拟机提供的轻量级的同步机制
* 可见性
* 原子性
* 有序性
