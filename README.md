# springboot_cache
缓存的使用

在多线程高并发场景中往往是离不开cache的，需要根据不同的应用场景来需要选择不同的cache，  
比如分布式缓存如redis、memcached，还有本地（进程内）缓存如ehcache、GuavaCache。  
之前用spring cache的时候集成的是ehcache，但接触到GuavaCache之后，被它的简单、强大、及轻量级所吸引。  
它不需要配置文件，使用起来和ConcurrentHashMap一样简单，而且能覆盖绝大多数使用cache的场景需求！

GuavaCache是google开源java类库Guava的其中一个模块，在maven工程下使用可在pom文件加入如下依赖
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>19.0</version>
        </dependency>

com.google.common.cache;  
适用场景：  
    你愿意消耗一些内存空间来提升速度。  
    你预料到某些键会被查询一次以上。  
    缓存中存放的数据总量不会超出内存容量。（Guava Cache是单个应用运行时的本地缓存。它不把数据存放到文件或外部服务器。如果这不符合你的需求，请尝试Memcached这类工具）  
一个残酷的现实是，我们几乎一定没有足够的内存缓存所有数据。你必须决定：什么时候某个缓存项就不值得保留了？Guava Cache提供了三种基本的缓存回收方式：基于容量回收、定时回收和基于引用回收以及显式的清除
1. 基于容量回收  
    如果要规定缓存项的数目不超过固定值，只需使用CacheBuilder.maximumSize(long)。缓存将尝试回收最近没有使用或总体上很少使用的缓存项。——警告：在缓存项的数目达到限定值之前，缓存就可能进行回收操作——通常来说，这种情况发生在缓存项的数目逼近限定值时。
2. 定时回收  
    CacheBuilder提供两种定时回收的方法：  
        expireAfterAccess(long, TimeUnit)：缓存项在给定时间内没有被读/写访问，则回收。请注意这种缓存的回收顺序和基于大小回收一样。  
        示例：Cache<String, UserBranchDto> transferCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();  
        expireAfterWrite(long, TimeUnit)：缓存项在给定时间内没有被写访问（创建或覆盖），则回收。如果认为缓存数据总是在固定时候后变得陈旧不可用，这种回收方式是可取的。  
        示例：Cache<Long, Set<String>> roleIdUrlsCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();
3. 基于引用得回收  
    通过使用弱引用的键、或弱引用的值、或软引用的值，Guava Cache可以把缓存设置为允许垃圾回收：  
        CacheBuilder.weakKeys()：使用弱引用存储键。当键没有其它（强或软）引用时，缓存项可以被垃圾回收。因为垃圾回收仅依赖恒等式（==），使用弱引用键的缓存用==而不是equals比较键。  
        CacheBuilder.weakValues()：使用弱引用存储值。当值没有其它（强或软）引用时，缓存项可以被垃圾回收。因为垃圾回收仅依赖恒等式（==），使用弱引用值的缓存用==而不是equals比较值。  
        CacheBuilder.softValues()：使用软引用存储值。软引用只有在响应内存需要时，才按照全局最近最少使用的顺序回收。考虑到使用软引用的性能影响，我们通常建议使用更有性能预测性的缓存大小限定（见上文，基于容量回收）。使用软引用值的缓存同样用==而不是equals比较值。  
显式的清除  
    个别清除：Cache.invalidate(key)  
    批量清除：Cache.invalidateAll(keys)  
    清除所有缓存项：Cache.invalidateAll()  
刷新机制：包括refresh和expire刷新机制  
expireAfterAccess: 当缓存项在指定的时间段内没有被读或写就会被回收。  
expireAfterWrite：当缓存项在指定的时间段内没有更新就会被回收。  
refreshAfterWrite：当缓存项上一次更新操作之后的多久会被刷新。


	import com.google.common.cache.Cache;
	import com.google.common.cache.CacheBuilder;
	//  角色和url列表的缓存
    Cache<Long, Set<String>> roleIdUrlsCache =
            CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES)
                    .build();
