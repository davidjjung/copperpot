package com.davigj.copperpot.core.setup;

import com.davigj.copperpot.common.blocks.BakedAlaskaBlock;
import com.davigj.copperpot.core.registry.CopperPotBlocks;
import com.davigj.copperpot.core.registry.CopperPotItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import vectorwing.farmersdelight.utils.tags.ModTags;

@EventBusSubscriber(modid = "copperpot")
public class CopperPotEvents {

    public CopperPotEvents() {}


    @SubscribeEvent
    public static void onCakeInteraction(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = event.getWorld().getBlockState(pos);
        ItemStack tool = event.getPlayer().getHeldItem(event.getHand());
        if (state.getBlock() == CopperPotBlocks.BAKED_ALASKA_BLOCK.get() && ModTags.KNIVES.contains(tool.getItem())) {
            int bites = (Integer)state.get(BakedAlaskaBlock.BITES);
            if (bites < 3) {
                world.setBlockState(pos, (BlockState)state.with(BakedAlaskaBlock.BITES, bites + 1), 3);
            } else {
                world.removeBlock(pos, false);
            }

            InventoryHelper.spawnItemStack(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), new ItemStack((IItemProvider) CopperPotItems.BAKED_ALASKA_SLICE.get()));
            world.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_WOOL_BREAK, SoundCategory.PLAYERS, 0.8F, 0.8F);
            event.setCancellationResult(ActionResultType.SUCCESS);
            event.setCanceled(true);
        }

    }
}
