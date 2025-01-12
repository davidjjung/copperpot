package com.davigj.copperpot;

import com.davigj.copperpot.core.registry.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CopperPot.MODID)
public class CopperPot
{
    public static final String MODID = "copperpot";
//    public static final Logger LOGGER = LogManager.getLogger();

    public CopperPot() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CopperPotConfig.COMMON_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CopperPotConfig.CLIENT_SPEC);

        CPItems.ITEMS.register(modEventBus);
        CPBlocks.BLOCKS.register(modEventBus);

        CPBlockEntityTypes.TILES.register(modEventBus);
        CPMenuTypes.MENU_TYPES.register(modEventBus);
        CPRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);

        CPRecipeTypes.RECIPE_TYPES.register(modEventBus);
        CPCreativeTab.TABS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }
}
