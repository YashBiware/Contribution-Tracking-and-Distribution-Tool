package com.ctdt.concurrency;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
@Component
public class ProjectLockManager {
    private final ConcurrentHashMap<Long,ReentrantLock> locks=new ConcurrentHashMap<>();
    private ReentrantLock lockFor(Long id){return locks.computeIfAbsent(id,i->new ReentrantLock(true));}
    public boolean tryLock(Long id) throws InterruptedException{return lockFor(id).tryLock(5L,TimeUnit.SECONDS);}
    public void unlock(Long id){ReentrantLock l=locks.get(id);if(l!=null&&l.isHeldByCurrentThread())l.unlock();}
    public boolean isLocked(Long id){ReentrantLock l=locks.get(id);return l!=null&&l.isLocked();}
    public int getQueueLength(Long id){ReentrantLock l=locks.get(id);return l!=null?l.getQueueLength():0;}
}
