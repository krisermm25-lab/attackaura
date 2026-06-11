package com.example.attackaura;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("attackaura")
public class AttackAuraMod {
    public AttackAuraMod() {
        MinecraftForge.EVENT_BUS.register(new AttackAuraHandler());
    }
}
