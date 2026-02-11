package com.doulrion.rima.interfaces;

public interface IntfLockableContainerBlockEntity {
    public void setKey(String key);

    public String getKey();

    public boolean isLocked();

    public boolean doesUnlock(String key);
}
