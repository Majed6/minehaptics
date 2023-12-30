package com.minehaptics.haptics;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(MineHaptics.MODID)
public class MineHaptics {
    public static final String MODID = "minehaptics";

    public MineHaptics() {
        MyForgeEventHandler myForgeEventHandler = new MyForgeEventHandler();
        MinecraftForge.EVENT_BUS.register(myForgeEventHandler);
    }
}