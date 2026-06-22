package com.doulrion.rima.component;

import java.util.ArrayList;

import net.minecraft.world.GameMode;

public class RimaGameModes extends ArrayList<GameMode>{
    public RimaGameModes() {
      super(0);
    }

    public RimaGameModes(int initialCapacity){
      super(initialCapacity);
    }

    public RimaGameModes(GameMode[] arr){
      super(arr.length);
      for (GameMode mode : arr){
        this.add(mode);
      }
    }

    public RimaGameModes clone(){
      RimaGameModes modes = new RimaGameModes(this.size());
      for (GameMode mode : this){
        modes.add(mode);
      }
      return modes;
    }
    
    public String toFormatString(String template){
      return template.replace("s", contains(GameMode.SURVIVAL) ? "X" : "_")
        .replace("a", contains(GameMode.ADVENTURE) ? "X" : "_")
        .replace("c", contains(GameMode.CREATIVE) ? "X" : "_")
        .replace("p", contains(GameMode.SPECTATOR) ? "X" : "_");
    }
  }
