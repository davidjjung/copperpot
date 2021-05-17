package com.davigj.copperpot.core.utils;

import com.davigj.copperpot.core.CopperPotMod;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TextUtils {
        public static IFormattableTextComponent getTranslation(String key, Object... args) {
            return new TranslationTextComponent(CopperPotMod.MOD_ID + "." + key, args);
        }
    }

