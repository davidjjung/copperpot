package com.davigj.copperpot.core.registry;

import com.davigj.copperpot.CopperPot;
import com.teamabnormals.blueprint.core.util.item.CreativeModeTabContentsPopulator;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import vectorwing.farmersdelight.common.registry.ModCreativeTabs;

@Mod.EventBusSubscriber(modid = CopperPot.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CPCreativeTab {

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CopperPot.MODID);

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        buildMainTabContents(event);
        buildFoodAndDrinksTabContents(event);
    }

    private static void buildMainTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() != ModCreativeTabs.TAB_FARMERS_DELIGHT.get()) return;

        event.accept(CPItems.COPPER_POT.get());
    }

    private static void buildFoodAndDrinksTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(CPItems.MERINGUE_BLOCK.get());
        }

        if (event.getTabKey() != CreativeModeTabs.FOOD_AND_DRINKS) return;

        acceptFoodAndDrinkItems(event);
    }

    private static boolean isModLoaded(String modid) {
        return (ModList.get().isLoaded(modid));
    }


    private static void acceptFoodAndDrinkItems(BuildCreativeModeTabContentsEvent event) {
//      CreativeModeTabContentsPopulator.mod(CopperPot.MODID).tab(CreativeModeTabs.FOOD_AND_DRINKS)
//                      .addItemsAfter();
        event.accept(CPItems.RAW_MERINGUE.get());
        event.accept(CPItems.MERINGUE.get());
        event.accept(CPItems.AUTUMNAL_AGAR.get());
        event.accept(CPItems.AESTIVAL_AGAR.get());
        event.accept(CPItems.BRUMAL_AGAR.get());
        event.accept(CPItems.VERNAL_AGAR.get());
        event.accept(CPItems.CARROT_CUPCAKE.get());
//        event.accept(CPItems.INCENDIARY_MERINGUE.get());

        if (isModLoaded("neapolitan")) {
            event.accept(CPItems.BAKED_ALASKA_BLOCK.get());
            event.accept(CPItems.BAKED_ALASKA_SLICE.get());
            event.accept(CPItems.PEPPERMINT_BARK_MERINGUE.get());
        }

//        if (isModLoaded("abundance") && isModLoaded("fruitful")) {
//            event.accept(CPItems.SPICED_APPLE_JAM.get());
//            event.accept(CPItems.PORK_SANDWICH.get());
//        }

        if (isModLoaded("savage_and_ravage")) {
            event.accept(CPItems.CREEPING_YOGURT.get());
            event.accept(CPItems.SOURDOUGH.get());
        }

        if (isModLoaded("buzzier_bees")) {
            event.accept(CPItems.ROYAL_JELLY.get());
        }

//        if (isModLoaded("neapolitan") && (isModLoaded("bayou_blues") || isModLoaded("environmental"))) {
//            event.accept(CPItems.MOONCAKE.get());
//        }

        if (isModLoaded("neapolitan") && isModLoaded("atmospheric")) {
            event.accept(CPItems.TROPICAL_MERINGUE.get());
        }

//        if (isModLoaded("upgrade_aquatic")) {
//            event.accept(CPItems.GUARDIAN_SOUFFLE.get());
//        }
    }
}
