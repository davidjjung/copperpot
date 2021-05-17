package com.davigj.copperpot.core.registry;

import com.davigj.copperpot.common.tile.container.CopperPotContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CopperPotContainerTypes {
    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES;
    public static final RegistryObject<ContainerType<CopperPotContainer>> COPPER_POT;

    public CopperPotContainerTypes() {
    }

    static {
        CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, "copperpot");
        COPPER_POT = CONTAINER_TYPES.register("copper_pot", () -> {
            return IForgeContainerType.create(CopperPotContainer::new);
        });
    }
}