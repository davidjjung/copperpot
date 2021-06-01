package com.davigj.copperpot.common.tile.container;

import com.davigj.copperpot.common.tile.CopperPotTileEntity;
import com.davigj.copperpot.core.registry.CopperPotBlocks;
import com.davigj.copperpot.core.registry.CopperPotContainerTypes;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Objects;

public class CopperPotContainer extends Container {
    public static final ResourceLocation EMPTY_CONTAINER_SLOT_BOWL = new ResourceLocation("copperpot", "item/empty_container_slot_bowl");
    public final CopperPotTileEntity tileEntity;
    public final ItemStackHandler inventoryHandler;
    private final IIntArray copperPotData;
    private final IWorldPosCallable canInteractWithCallable;

    public CopperPotContainer(int windowId, PlayerInventory playerInventory, CopperPotTileEntity tileEntity, IIntArray copperPotDataIn) {
        super((ContainerType) CopperPotContainerTypes.COPPER_POT.get(), windowId);
        this.tileEntity = tileEntity;
        this.inventoryHandler = tileEntity.getInventory();
        this.copperPotData = copperPotDataIn;
        this.canInteractWithCallable = IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos());
        int startX = 8;
        int startY = 18;

        int inputStartX = 30;
        int inputStartY = 26;
        int borderSlotSize = 18;

        // meal inventory slots (top left)
        for (int row = 0; row < 1; ++row) {
            for (int column = 0; column < 3; ++column) {
                this.addSlot(new SlotItemHandler(inventoryHandler, (row * 3) + column,
                        inputStartX + (column * borderSlotSize),
                        inputStartY + (row * borderSlotSize)));
            }
        }

        this.addSlot(new CopperPotMealSlot(this.inventoryHandler, 3, 124, 26));
        this.addSlot(new SlotItemHandler(this.inventoryHandler, 4, 92, 55) {
            @OnlyIn(Dist.CLIENT)
            public Pair<ResourceLocation, ResourceLocation> getBackground() {
                return Pair.of(PlayerContainer.LOCATION_BLOCKS_TEXTURE, CopperPotContainer.EMPTY_CONTAINER_SLOT_BOWL);
            }
        });
        this.addSlot(new CopperPotResultSlot(this.inventoryHandler, 5, 124, 55));

		// Main Player Inventory
		int startPlayerInvY = startY * 4 + 12;
		for (int row = 0; row < 3; ++row) {
			for (int column = 0; column < 9; ++column) {
				this.addSlot(new Slot(playerInventory, 9 + (row * 9) + column, startX + (column * borderSlotSize),
						startPlayerInvY + (row * borderSlotSize)));
			}
		}

		// Hotbar
        for (int column = 0; column < 9; ++column) {
            this.addSlot(new Slot(playerInventory, column, startX + (column * borderSlotSize), 142));
        }
        this.trackIntArray(copperPotDataIn);
    }

    private static CopperPotTileEntity getTileEntity(PlayerInventory playerInventory, PacketBuffer data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());
        if (tileAtPos instanceof CopperPotTileEntity) {
            return (CopperPotTileEntity)tileAtPos;
        } else {
            throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
        }
    }

    public CopperPotContainer(int windowId, PlayerInventory playerInventory, PacketBuffer data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data), new IntArray(4));
    }

    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(this.canInteractWithCallable, playerIn, (Block) CopperPotBlocks.COPPER_POT.get());
    }

    // determines how the player inventory looks, at least, I think
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        int indexMealDisplay = 3;
        int indexContainerInput = 4;
        int indexOutput = 5;
        int startPlayerInv = indexOutput + 1;
        int endPlayerInv = startPlayerInv + 36;
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index == indexOutput) {
                if (!this.mergeItemStack(itemstack1, startPlayerInv, endPlayerInv, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index > indexOutput) {
                if (itemstack1.getItem() == Items.BOWL && !this.mergeItemStack(itemstack1, indexContainerInput, indexContainerInput + 1, false)) {
                    return ItemStack.EMPTY;
                }
                if (!this.mergeItemStack(itemstack1, 0, indexMealDisplay, false)) {
                    return ItemStack.EMPTY;
                }
                if (!this.mergeItemStack(itemstack1, indexContainerInput, indexOutput, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, startPlayerInv, endPlayerInv, false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, itemstack1);
        }
        return itemstack;
    }

    @OnlyIn(Dist.CLIENT)
    public int getCookProgressionScaled() {
        int i = this.copperPotData.get(0);
        int j = this.copperPotData.get(1);
        return j != 0 && i != 0 ? i * 24 / j : 0;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isHeated() {
        return this.tileEntity.isAboveLitHeatSource();
    }

    @OnlyIn(Dist.CLIENT)
    public boolean hasEffect() { return this.tileEntity.isEffectTrue(); }
}