package com.doulrion.rima.component;

import java.util.UUID;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.doulrion.rima.Rima;
import com.doulrion.rima.item.LockItems;
import com.doulrion.rima.item.custom.KeyItem;
import com.doulrion.rima.item.custom.LockItem;
import com.doulrion.rima.interfaces.ILockableRimaEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.GameMode;
import net.minecraft.text.Text;

import java.util.ArrayList;

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

  private class GameModes extends ArrayList<GameMode>{
    public GameModes() {
      super(0);
    }
    public GameModes(int initialCapacity){
      super(initialCapacity);
    }
    public GameModes(GameMode[] arr){
      super(arr.length);
      for (GameMode mode : arr){
        this.add(mode);
      }
    }
    public GameModes clone(){
      GameModes modes = new GameModes(this.size());
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

  public class Helper{
    public static UUID getKeyFromStack(ItemStack Stack){
      String itemKey = Stack.get(RimaDataComponentTypes.RIMA_LOCK);
      if (itemKey == null){
        return null;
      }
      try {
        return UUID.fromString(itemKey);
      } catch (Exception e) {
        Rima.LOGGER.error("Could not parse invalid lock item UUID: " + itemKey, e);
        return null;
      }
    }

    public static GameMode getPlayerGamemode(PlayerEntity player){
      if (player.isCreative()){
        return GameMode.CREATIVE;
      } else if (player.isSpectator()) {
        return GameMode.SPECTATOR;
      } else if (player.canModifyBlocks()){
        return GameMode.SURVIVAL;
      } else {
        return GameMode.ADVENTURE;
      }
    }

    public static boolean isKeyItem(ItemStack stack){
      Rima.LOGGER.info("nuttne" + stack.getName());
      return stack.getItem().getClass().isAssignableFrom(KeyItem.class) && !stack.isOf(LockItems.LOCKPICK_ITEM);  // idk why. give up and just check for lockpick :P
    }  
    
    public static boolean isLockItem(ItemStack stack){
      return stack.getItem().getClass().isAssignableFrom(LockItem.class) && !stack.isOf(LockItems.LOCKPICK_ITEM);  // idk why. give up and just check for lockpick :P
    }

    public static boolean isPickItem(ItemStack stack){
      return stack.isOf(LockItems.LOCKPICK_ITEM);
    }

    public static BlockPos normalizeBlockPos(BlockState state, World world, BlockPos pos){
      if (state.contains(net.minecraft.state.property.Properties.DOUBLE_BLOCK_HALF) && 
        state.get(net.minecraft.state.property.Properties.DOUBLE_BLOCK_HALF) == net.minecraft.block.enums.DoubleBlockHalf.UPPER) {
          return pos.down();
      }
      return pos;
    }

  }

  private UUID key = null;
  private float pickRate = 0;
  private GameModes GameModeAdd       = null;
  private GameModes GameModeRemove    = null;
  private GameModes GameModeUse       = null;
  private GameModes GameModePick      = null;
  private GameModes GameModeBypassUse = null;
  private Item originItem = null;


////////////////////////////////////
//    Constructors & Defaults     //
////////////////////////////////////

  public RimaLockState(){
    super();
    GameModeAdd       = new GameModes();
    GameModeRemove    = new GameModes();
    GameModeUse       = new GameModes();
    GameModePick      = new GameModes();
    GameModeBypassUse = new GameModes();
  }

  public RimaLockState(UUID key, float pickRate, Item originItem, RimaLockState.GameModes Add, RimaLockState.GameModes Remove, RimaLockState.GameModes Use, RimaLockState.GameModes Pick, RimaLockState.GameModes BypassUse){
    super();
    this.key = key;
    this.pickRate = pickRate;
    this.originItem = originItem;
    this.GameModeAdd = Add;
    this.GameModeAdd = Remove;
    this.GameModeAdd = Use;
    this.GameModeAdd = Pick;
    this.GameModeAdd = BypassUse;
  }

  public RimaLockState(UUID key, float pickRate, Item originItem, GameMode[] Add, GameMode[] Remove, GameMode[] Use, GameMode[] Pick, GameMode[] BypassUse){
    super();
    this.key = key;
    this.pickRate = pickRate;
    this.originItem = originItem;
    this.GameModeAdd        = new GameModes(Add);
    this.GameModeRemove     = new GameModes(Remove);
    this.GameModeUse        = new GameModes(Use);
    this.GameModePick       = new GameModes(Pick);
    this.GameModeBypassUse  = new GameModes(BypassUse);
  }

  public RimaLockState(RimaLockState original){
    super();
    this.key = original.key;
    this.pickRate = original.pickRate;
    this.originItem = original.originItem;
    this.GameModeAdd = original.GameModeAdd == null ? new GameModes() : original.GameModeAdd.clone();
    this.GameModeRemove = original.GameModeRemove == null ? new GameModes() : original.GameModeRemove.clone();
    this.GameModeUse = original.GameModeUse == null ? new GameModes() : original.GameModeUse.clone();
    this.GameModePick = original.GameModePick == null ? new GameModes() : original.GameModePick.clone();
    this.GameModeBypassUse = original.GameModeBypassUse == null ? new GameModes() : original.GameModeBypassUse.clone();
  }
  
  public static RimaLockState fromLockItem(ItemStack Item){   // get state from Item
    UUID itemKey = Helper.getKeyFromStack(Item);
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
    return new RimaLockState(this);
  }


////////////////////////////////////
//          Save & Load           //
////////////////////////////////////

  private void writeGameModeList(NbtCompound nbt, GameModes list, String nbtKey){
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

  private void readGameModeList(NbtCompound nbt, GameModes list, String nbtKey){
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
//          Misc functions        //
////////////////////////////////////

  public boolean isLocked(){
    return key != null;
  }

  public UUID getKey(){
    return key;
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

  private boolean unlockableBy(ItemStack Stack){
    if (Stack.isOf(LockItems.ADMIN_KEY_ITEM)){
      return true;
    }
    UUID heldkey = Helper.getKeyFromStack(Stack);
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


////////////////////////////////////
//        Action Handling         //
////////////////////////////////////

// try LockPick & remove item durability
  private boolean tryLockPick(ItemStack stack, PlayerEntity player){
    float rnd = player.getRandom().nextFloat();
    player.sendMessage(Text.of("random: " + Float.toString(rnd))); 
    if (rnd < pickRate) {
      return true;
    } else {      // keep lockpick when succeeding
      stack.damage(Math.max(1, stack.getMaxDamage() / 2), player, EquipmentSlot.MAINHAND);
    }
    return false;
  }

// check, remove Lock & spawn item
  private static ActionResult doRemoveLock(ILockableRimaEntity lockableEntity, RimaLockState lockState, World world, BlockPos pos, PlayerEntity player, GameMode gameMode, ItemStack held){
    if (!lockState.GameModeRemove.contains(gameMode)){  // check if gamemode allows unlocking
      player.sendMessage(Text.translatable("message.rima.remove_not_allowed"), true);
    } else if (!lockState.unlockableBy(held)){
      player.sendMessage(Text.translatable("message.rima.wrong_key"), true);
    } else {
      ItemStack stack = lockState.toItemStack();  // get item before.
      lockableEntity.setLockState(new RimaLockState());  // do unlock
      ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack); // drop item
      player.sendMessage(Text.translatable("message.rima.removed_lock"), true);
    }
    return ActionResult.SUCCESS;
  }

// check & open
  private void doOpenLock(ILockableRimaEntity lockableEntity, World world, BlockPos pos, PlayerEntity player, GameMode gameMode, ItemStack held, CallbackInfoReturnable<ActionResult> cir){
    if (!GameModeUse.contains(gameMode)){
      player.sendMessage(Text.translatable("message.rima.open_not_allowed"), true);
      cir.setReturnValue(ActionResult.SUCCESS);
    } else if (!unlockableBy(held)) {
      player.sendMessage(Text.translatable("message.rima.wrong_key"), true);
      cir.setReturnValue(ActionResult.SUCCESS);
    } 
    // do nothing on success
  }

// check & pick
  private void doPickLock(ILockableRimaEntity lockableEntity, World world, BlockPos pos, PlayerEntity player, GameMode gameMode, ItemStack held, CallbackInfoReturnable<ActionResult> cir){
    if (!GameModePick.contains(gameMode)){
      player.sendMessage(Text.translatable("message.rima.pick_not_allowed"), true);
      cir.setReturnValue(ActionResult.SUCCESS);
    } else if (!tryLockPick(held, player)){
      player.sendMessage(Text.translatable("message.rima.pick_failed"), true);
      cir.setReturnValue(ActionResult.SUCCESS);
    } 
    // do nothing on success
  }

// useOnBlock action for KeyItem
  public static ActionResult useOnBlockKey(ItemUsageContext context){
    PlayerEntity player = context.getPlayer();
    if (!player.isSneaking()){
      return ActionResult.PASS;
    }
    ItemStack stack = context.getStack();
    GameMode gameMode = Helper.getPlayerGamemode(player);
    World world = context.getWorld();
    BlockPos pos = Helper.normalizeBlockPos(context.getWorld().getBlockState(context.getBlockPos()), world, context.getBlockPos());

    if (!(world.getBlockEntity(pos) instanceof ILockableRimaEntity le)){
      return ActionResult.PASS;
    }
    RimaLockState lockState = le.getLockState();  // not locked.
    if (lockState == null || !lockState.isLocked()){
      return ActionResult.PASS;
    }

    if (Helper.isKeyItem(stack)){
      return doRemoveLock(le, lockState, world, pos, player, gameMode, stack);
    }
    return ActionResult.SUCCESS;
  }

// useOnBlock action for LockItem
  public static ActionResult useOnBlockLock(ItemUsageContext context){
    PlayerEntity player = context.getPlayer();
    if (!player.isSneaking()){
      return ActionResult.PASS;
    }
    ItemStack stack = context.getStack();
    GameMode gameMode = Helper.getPlayerGamemode(player);
    World world = context.getWorld();
    BlockPos pos = Helper.normalizeBlockPos(context.getWorld().getBlockState(context.getBlockPos()), world, context.getBlockPos());

    if (!(world.getBlockEntity(pos) instanceof ILockableRimaEntity le)){
      return ActionResult.PASS;
    }
    
    if (le.getLockState().isLocked()){
      player.sendMessage(Text.translatable("message.rima.is_locked"), true);
      return ActionResult.SUCCESS;
    }

    RimaLockState heldLockState = RimaLockState.fromLockItem(stack);

    if (!heldLockState.GameModeAdd.contains(gameMode)){
      player.sendMessage(Text.translatable("message.rima.add_not_allowed"), true);
      return ActionResult.SUCCESS;
    }

    if (!heldLockState.isLocked()){
      player.sendMessage(Text.translatable("message.rima.invalid_lock"), true);
      return ActionResult.SUCCESS;
    }
    
    le.setLockState(heldLockState.clone());    // simply clone lockstate to lock.
    
    player.sendMessage(Text.translatable("message.rima.locked"), true);
    stack.decrement(1);

    return ActionResult.SUCCESS;
  }

// checkUnlocked function for LockableContainerBlockEntity
  public Boolean checkUnlocked(PlayerEntity player){
    if (!isLocked()){
      return true;
    }
    
    GameMode gameMode = Helper.getPlayerGamemode(player);
    ItemStack stack = player.getMainHandStack();

    if (player.isSneaking() && stack.isEmpty() && gameMode == GameMode.CREATIVE){
      player.sendMessage(Text.of(debugString()), false);
      return false;
    }

    if (GameModeBypassUse.contains(gameMode)){
      player.sendMessage(Text.translatable("message.rima.bypassed"), true);
      return true;
    }

    if (Helper.isKeyItem(stack)){
      if (!GameModeUse.contains(gameMode)){
        player.sendMessage(Text.translatable("message.rima.open_not_allowed"), true);
        return false;
      } else if (!unlockableBy(stack)) {
        player.sendMessage(Text.translatable("message.rima.wrong_key"), true);
        return false;
      }
      return true;
    } else if (Helper.isPickItem(stack)){
      if (!GameModePick.contains(gameMode)){
        player.sendMessage(Text.translatable("message.rima.pick_not_allowed"), true);
        return false;
      } else if (!tryLockPick(stack, player)){
        player.sendMessage(Text.translatable("message.rima.pick_failed"), true);
        return false;
      }
      return true;
    }
    player.sendMessage(Text.translatable("message.rima.is_locked"), true);
    return false;
  }

// onUseGenericBlock for Generic block with LockableEntity
  public static void onUseGenericBlock(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
    if (world.isClient) return;   // do not handle on client.

    pos = Helper.normalizeBlockPos(state, world, pos);
      
    BlockEntity be = world.getBlockEntity(pos);

    if (be instanceof ILockableRimaEntity le){    // ignore non lockable entity
      le.getLockState().onUseGenericBlock_(le, world, pos, player, cir);
    }
  } 

// onUseGenericBlock once context has been established
  public void onUseGenericBlock_(ILockableRimaEntity lockableEntity, World world, BlockPos pos, PlayerEntity player, CallbackInfoReturnable<ActionResult> cir){
    ItemStack held = player.getMainHandStack();

    GameMode gameMode = Helper.getPlayerGamemode(player); 

    if (!isLocked()){
      return; // unlocked and no lock. let vanilla handle it
    }

    if (player.isSneaking()){   // try unlock
      if (held.isEmpty() && gameMode == GameMode.CREATIVE){
        player.sendMessage(Text.of(debugString()), false);
        cir.setReturnValue(ActionResult.SUCCESS);
      } else {
        cir.setReturnValue(ActionResult.FAIL);  // allow placing of blocks
      }
    } else {
      if (GameModeBypassUse.contains(gameMode)){  // bypass using 
        player.sendMessage(Text.translatable("message.rima.bypassed"), true);
        // do nothing. = allow
      } else if (Helper.isKeyItem(held)){
        doOpenLock(lockableEntity, world, pos, player, gameMode, held, cir);
      } else if (Helper.isPickItem(held)){
        doPickLock(lockableEntity, world, pos, player, gameMode, held, cir);
      } else {
        player.sendMessage(Text.translatable("message.rima.is_locked"), true);
        cir.setReturnValue(ActionResult.CONSUME);
      }
    }    
    
    // cir.setReturnValue(ActionResult.FAIL);
  }

// neighborUpdate for GenericBlock
  public static void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify, CallbackInfo cir){
    if (world.getBlockEntity(Helper.normalizeBlockPos(state, world, sourcePos)) instanceof ILockableRimaEntity doorEntity && doorEntity.getLockState().isLocked()) {
      // Prevent BlockUpdate from opening the door if it's locked. Only Player can Unlock
      cir.cancel();
      return;
    }
  }

}
