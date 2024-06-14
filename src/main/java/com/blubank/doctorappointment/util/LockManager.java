package com.blubank.doctorappointment.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024 
 */
@Component
public class LockManager {
    private final ConcurrentHashMap<Long, Lock> lockMap = new ConcurrentHashMap<>();

    public Lock getLock(Long appointmentId) {
        lockMap.putIfAbsent(appointmentId, new ReentrantLock());
        return lockMap.get(appointmentId);
    }

    public void removeLock(Long appointmentId) {
        lockMap.remove(appointmentId);
    }
}