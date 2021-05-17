package com.davigj.copperpot.common.tile.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CopperPotItemHandler implements IItemHandler {
    //remember to change the funny numbers !!
//    6 7 8 hohoh;
    private static final int SLOTS_INPUT = 3;
    private static final int SLOT_CONTAINER_INPUT = 4;
    private static final int SLOT_MEAL_OUTPUT = 5;
    private final IItemHandler itemHandler;
    private final Direction side;

    public CopperPotItemHandler(IItemHandler itemHandler, @Nullable Direction side) {
        this.itemHandler = itemHandler;
        this.side = side;
    }

    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return this.itemHandler.isItemValid(slot, stack);
    }

    public int getSlots() {
        return this.itemHandler.getSlots();
    }

    @Nonnull
    public ItemStack getStackInSlot(int slot) {
        return this.itemHandler.getStackInSlot(slot);
    }

    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (this.side != null && !this.side.equals(Direction.UP)) {
            // 7
            return slot == 4 ? this.itemHandler.insertItem(slot, stack, simulate) : stack;
        } else {
            // 6
            return slot < 3 ? this.itemHandler.insertItem(slot, stack, simulate) : stack;
        }
    }

    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (this.side != null && !this.side.equals(Direction.UP)) {
            // 8
            return slot == 5 ? this.itemHandler.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
        } else {
            // 6
            return slot < 3 ? this.itemHandler.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
        }
    }

    public int getSlotLimit(int slot) {
        return this.itemHandler.getSlotLimit(slot);
    }
}
