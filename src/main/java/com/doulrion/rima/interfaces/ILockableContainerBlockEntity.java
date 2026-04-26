package com.doulrion.rima.interfaces;

public interface ILockableContainerBlockEntity {
    public void setKey(String key);

    public String getKey();

    public void setAdminLocked(boolean adminLocked);

    public boolean isAdminLocked();

    public boolean isLocked();

    public boolean doesUnlock(String key);
}
