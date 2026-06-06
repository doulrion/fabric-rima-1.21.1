package com.doulrion.rima.interfaces;

public interface ILockableDoor {
    void setKey(String key);
    String getKey();
    public void setAdminLocked(boolean adminLocked);
    public boolean isAdminLocked();
    boolean isLocked();
    boolean doesUnlock(String key);
}