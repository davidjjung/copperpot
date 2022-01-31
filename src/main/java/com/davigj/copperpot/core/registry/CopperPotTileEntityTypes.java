package com.davigj.copperpot.core.registry;

import com.davigj.copperpot.common.tile.CopperPotTileEntity;
import com.mojang.datafixers.types.Type;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CopperPotTileEntityTypes {
    public static final DeferredRegister<TileEntityType<?>> TILES;
    public static final RegistryObject<TileEntityType<CopperPotTileEntity>> COPPER_POT_TILE;
    public CopperPotTileEntityTypes() {    }
    static {
        TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, "copperpot");
        COPPER_POT_TILE = TILES.register("copper_pot", () -> {
            return TileEntityType.Builder.of(CopperPotTileEntity::new, new Block[]{(Block)CopperPotBlocks.COPPER_POT.get()}).build((Type)null);
        });
    }
}
