package com.example.attackaura;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("attackaura")
public class AttackAuraMod {
    public AttackAuraMod() {
        FMLJavaModLoadingContext.get().getModEventBus()
            .addListener(AttackAuraHandler::registerKey);
        MinecraftForge.EVENT_BUS.register(new AttackAuraHandler());
    }
}
