package com.doulrion.rima.interfaces;

import com.doulrion.rima.component.RimaLockState;


public interface ILockableRimaEntity {
    RimaLockState getLockState();
    void setLockState(RimaLockState state);
}