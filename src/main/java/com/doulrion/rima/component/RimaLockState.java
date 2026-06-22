package com.doulrion.rima.component;

import java.util.UUID;

import com.doulrion.rima.Rima;
import com.doulrion.rima.item.LockItems;
import com.doulrion.rima.item.custom.LockItem;
import com.doulrion.rima.interfaces.ILockableRimaEntity;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.GameMode;

public class RimaLockState extends Object {
  private static final String RIMA_NBT_KEY       = "rima_key";
  private static final String RIMA_NBT_PICKRATE       = "rima_pickrate";
  private static final String RIMA_NBT_CANADDLOCK = "rima_canAddLock";
  private static final String RIMA_NBT_CANREMOVELOCK = "rima_canRemoveLock";
  private static final String RIMA_NBT_CANUSELOCK    = "rima_canUseLock";
  private static final String RIMA_NBT_CANPICKLOCK    = "rima_canPickLock";
  private static final String RIMA_NBT_CANBYPASSUSELOCK    = "rima_canBypassUseLock";
  private static final String RIMA_NBT_ORIGINITEM = "rima_originItem";
  
  public static final UUID adminUUID = new UUID(0,0); // keep internal

  private UUID key = null;
  private float pickRate = 0F;
  private RimaGameModes GameModeAdd = null;
  private RimaGameModes GameModeRemove = null;
  private RimaGameModes GameModeUse = null;
  private RimaGameModes GameModePick = null;
  private RimaGameModes GameModeBypassUse = null;
  private Item originItem = null;


////////////////////////////////////
//    Constructors & Defaults     //
////////////////////////////////////

  public RimaLockState(){
    super();
    this.GameModeAdd        = new RimaGameModes();
    this.GameModeRemove     = new RimaGameModes();
    this.GameModeUse        = new RimaGameModes();
    this.GameModePick       = new RimaGameModes();
    this.GameModeBypassUse  = new RimaGameModes();
  }

  public RimaLockState(UUID key, float pickRate, Item originItem, RimaGameModes Add, RimaGameModes Remove, RimaGameModes Use, RimaGameModes Pick, RimaGameModes BypassUse){
    super();
    this.key = key;
    this.pickRate = pickRate;
    this.originItem = originItem;
    this.GameModeAdd = Add == null ? new RimaGameModes() : Add;
    this.GameModeRemove = Remove == null ? new RimaGameModes() : Remove;
    this.GameModeUse = Use == null ? new RimaGameModes() : Use;
    this.GameModePick = Pick == null ? new RimaGameModes() : Pick;
    this.GameModeBypassUse = BypassUse == null ? new RimaGameModes() : BypassUse;
  }

  public RimaLockState(UUID key, float pickRate, Item originItem, GameMode[] Add, GameMode[] Remove, GameMode[] Use, GameMode[] Pick, GameMode[] BypassUse){
    super();
    this.key                = key;
    this.pickRate           = pickRate;
    this.originItem         = originItem;
    this.GameModeAdd        = new RimaGameModes(Add);
    this.GameModeRemove     = new RimaGameModes(Remove);
    this.GameModeUse        = new RimaGameModes(Use);
    this.GameModePick       = new RimaGameModes(Pick);
    this.GameModeBypassUse  = new RimaGameModes(BypassUse);
  }
  
  public static RimaLockState fromLockItem(ItemStack Item){   // get state from Item
    UUID itemKey = RimaHelper.getKeyFromStack(Item);
    if (itemKey == null && Item.isOf(LockItems.ADMIN_LOCK_ITEM)){
      itemKey = adminUUID;
    }
    Item item_ = Item.getItem();
    if (LockItems.LockItems.contains(item_) && item_ instanceof LockItem lockItem){
      return new RimaLockState(itemKey, 
        lockItem.defaultPickRate, 
        item_, 
        lockItem.defaultGameModeAdd, 
        lockItem.defaultGameModeRemove, 
        lockItem.defaultGameModeUse, 
        lockItem.defaultGameModePick,
        lockItem.defaultGameModeBypassUse
      );
    }

    Rima.LOGGER.error("no lock preset for item found: " + Registries.ITEM.getEntry(Item.getItem()).getIdAsString());
    return null;
  }

  public RimaLockState clone(){
    return new RimaLockState(this.key, 
      this.pickRate,
      this.originItem,
      this.GameModeAdd.clone(),
      this.GameModeRemove.clone(),
      this.GameModeUse.clone(),
      this.GameModePick.clone(),
      this.GameModeBypassUse.clone()
    );
  }

////////////////////////////////////
//          Save & Load           //
////////////////////////////////////

  private void writeGameModeList(NbtCompound nbt, RimaGameModes list, String nbtKey){
    if ((list == null) || list.size() < 1){ // do not write empty list. delete existing instead
      nbt.remove(nbtKey);
      return;
    }
    int[] arr = new int[list.size()];   // convert to int array
    for (var i = 0; i < list.size(); i++){
      arr[i] = list.get(i).getId();
    }
    nbt.putIntArray(nbtKey, arr);
  }

  private void readGameModeList(NbtCompound nbt, RimaGameModes list, String nbtKey){
    list.clear();
    if (!nbt.contains(nbtKey)){
      return;
    }
    var arr = nbt.getIntArray(nbtKey);
    for (int i : arr){
      list.add(GameMode.byId(i));
    }
    return;
  }
  
  public void saveToEntityNbt(NbtCompound nbt){
    if (key != null) {
      nbt.putUuid(RIMA_NBT_KEY, key);
    } else {
      nbt.remove(RIMA_NBT_KEY);
    }
    nbt.putFloat(RIMA_NBT_PICKRATE, pickRate);
    writeGameModeList(nbt, GameModeAdd, RIMA_NBT_CANADDLOCK);
    writeGameModeList(nbt, GameModeRemove, RIMA_NBT_CANREMOVELOCK);
    writeGameModeList(nbt, GameModeUse, RIMA_NBT_CANUSELOCK);
    writeGameModeList(nbt, GameModePick, RIMA_NBT_CANPICKLOCK);
    writeGameModeList(nbt, GameModeBypassUse, RIMA_NBT_CANBYPASSUSELOCK);

    if (originItem != null){
      nbt.putString(RIMA_NBT_ORIGINITEM, Registries.ITEM.getEntry(originItem).getIdAsString());
    } else {
      nbt.remove(RIMA_NBT_ORIGINITEM);
    }
  }

  public void loadFromEntityNbt(NbtCompound nbt){
    if (nbt.contains(RIMA_NBT_KEY, NbtElement.INT_ARRAY_TYPE)) {
      key = nbt.getUuid(RIMA_NBT_KEY);
    } else {
      key = null;
    }

    if (nbt.contains(RIMA_NBT_PICKRATE, NbtElement.FLOAT_TYPE)){
      pickRate = nbt.getFloat(RIMA_NBT_PICKRATE);
    } else {
      pickRate = 0;
    }

    readGameModeList(nbt, GameModeAdd, RIMA_NBT_CANADDLOCK);
    readGameModeList(nbt, GameModeRemove, RIMA_NBT_CANREMOVELOCK);
    readGameModeList(nbt, GameModeUse, RIMA_NBT_CANUSELOCK);
    readGameModeList(nbt, GameModePick, RIMA_NBT_CANPICKLOCK);
    readGameModeList(nbt, GameModeBypassUse, RIMA_NBT_CANBYPASSUSELOCK);

    if (nbt.contains(RIMA_NBT_ORIGINITEM, NbtElement.STRING_TYPE)){
      String itemid = nbt.getString(RIMA_NBT_ORIGINITEM);
      try {
        originItem = Registries.ITEM.get(Identifier.of(itemid));
      } catch (Exception e) {
        Rima.LOGGER.error("could not get originitem with id: " + itemid, e);
        originItem = null;
      } 
    } else {
      originItem = null;
      if (isLocked()){
        Rima.LOGGER.warn("origin item has not been set. base lock will be dropped as fallback");
      }
    }
  }

////////////////////////////////////
//         GameMode Checks        //
////////////////////////////////////
  public boolean isGameModeAdd(GameMode mode){return GameModeAdd.contains(mode);}
  public boolean isGameModeRemove(GameMode mode){return GameModeRemove.contains(mode);}
  public boolean isGameModeUse(GameMode mode){return GameModeUse.contains(mode);}
  public boolean isGameModePick(GameMode mode){return GameModePick.contains(mode);}
  public boolean isGameModeBypassUse(GameMode mode){return GameModeBypassUse.contains(mode);}

////////////////////////////////////
//          Misc functions        //
////////////////////////////////////

  public boolean isLocked(){
    return key != null;
  }

  public boolean isAdmin(){
    return originItem == LockItems.ADMIN_LOCK_ITEM;
  }

  public boolean isPlayerRemovable(PlayerEntity player){
    if (player == null){
      return GameModeRemove.contains(GameMode.SURVIVAL); // assume survival
    } else {
      return GameModeRemove.contains(RimaHelper.getPlayerGamemode(player));
    }
  }

  public UUID getKey(){
    return key;
  }

  public boolean unlockableBy(ItemStack Stack){
    if (Stack.isOf(LockItems.ADMIN_KEY_ITEM)){
      return true;
    }
    UUID heldkey = RimaHelper.getKeyFromStack(Stack);
    if (heldkey != null && heldkey.equals(key)){
      return true;
    }
    return false;
  }

  private ItemStack toItemStack(){
    ItemStack droppedLock = null;
    if (originItem == null){
      Rima.LOGGER.error("origin item is zero! dropping basic lock");
      droppedLock = new ItemStack(LockItems.LOCK_ITEM);
    } else {
      droppedLock = new ItemStack(originItem);
    }
    if (key != null && originItem != LockItems.ADMIN_LOCK_ITEM){
        droppedLock.set(RimaDataComponentTypes.RIMA_LOCK, key.toString());
    }
    return droppedLock;
  }

  public String debugString(){
    return "Properties of lockstate: \n" 
        + "key:      " + (key == null ? "null" : key.toString()) + "\n"
        + "pickRate: " + Float.toString(pickRate) + "\n"
        + "LockItem: " + ((originItem == null) ? "null" : Registries.ITEM.getEntry(originItem).getIdAsString()) + "\n" 
        + "=========================== \n" 
        + "Gamemodes | s | a | c | sp \n" 
        + "=========================== \n" 
        + "_____ Add | " + GameModeAdd.toFormatString("s | a | c | p") + "  \n" 
        + "__ Remove | " + GameModeRemove.toFormatString("s | a | c | p") + "  \n" 
        + "_____ Use | " + GameModeUse.toFormatString("s | a | c | p") + "  \n" 
        + "_____ Pick | " + GameModePick.toFormatString("s | a | c | p") + "  \n" 
        + "__ Bypass | " + GameModeBypassUse.toFormatString("s | a | c | p") + "  ";
  }

////////////////////////////////////
//        Action Handling         //
////////////////////////////////////

// check, remove Lock & spawn item
  public ActionResult doRemoveLock(ILockableRimaEntity lockableEntity, World world, BlockPos pos, PlayerEntity player, GameMode gameMode, ItemStack held){
    if (!isGameModeRemove(gameMode)){  // check if gamemode allows unlocking
      RimaHelper.Messages.messageLockRemove_not_allowed(player);
    } else if (!unlockableBy(held)){
      RimaHelper.Messages.messageWrongKey(player);
    } else {
      ItemStack stack = toItemStack();  // get item before.
      lockableEntity.setLockState(new RimaLockState());  // do unlock
      ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack); // drop item
      RimaHelper.Messages.messageLockRemoved(player);
    }
    return ActionResult.SUCCESS;
  }

// check & open (returns true to abort action)
  public boolean doOpenLock(PlayerEntity player, GameMode gameMode, ItemStack held){
    if (!isGameModeUse(gameMode)){
      RimaHelper.Messages.messageUseNotAllowed(player);
      return true;
    } else if (!unlockableBy(held)) {
      RimaHelper.Messages.messageWrongKey(player);
      return true;
    } 
    return false;
  }

// check & pick (returns true to abort action)
  public boolean doPickLock(PlayerEntity player, GameMode gameMode, ItemStack held){
    if (!isGameModePick(gameMode)){
      RimaHelper.Messages.messagePickNotAllowed(player);
      return true;
    }
    float rnd = player.getRandom().nextFloat(); 
    if (rnd < pickRate){ // keep lockpick when succeeding
      return false;
    }      
    RimaHelper.Messages.messagePickFailed(player);
    held.damage(Math.max(1, held.getMaxDamage() / 2), player, EquipmentSlot.MAINHAND);
    return true;
  }

}
