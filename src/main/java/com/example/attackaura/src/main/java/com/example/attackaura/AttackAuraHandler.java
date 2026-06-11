package com.example.attackaura;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.UUID;

public class AttackAuraHandler {

    private static final double RADIUS = 5.0;
    private static final int    DELAY  = 10;

    private static boolean enabled = false;
    private int tick = 0;

    public static KeyBinding toggleKey;

    public static void registerKey(FMLClientSetupEvent event) {
        toggleKey = new KeyBinding(
            "key.attackaura.toggle",
            GLFW.GLFW_KEY_R,
            "key.categories.attackaura"
        );
        ClientRegistry.registerKeyBinding(toggleKey);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (toggleKey != null && toggleKey.consumeClick()) {
            enabled = !enabled;
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                mc.player.sendMessage(
                    new StringTextComponent(
                        enabled ? "\u00a7aАура включена" : "\u00a7cАура выключена"
                    ),
                    UUID.randomUUID()
                );
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!enabled) return;

        PlayerEntity player = event.player;
        World world = player.level;

        if (world.isClientSide) return;
        if (!player.isFallFlying()) return;

        tick++;
        if (tick % DELAY != 0) return;

        List<PlayerEntity> targets = world.getEntitiesOfClass(
            PlayerEntity.class,
            player.getBoundingBox().inflate(RADIUS),
            e -> e != player && e.isAlive()
        );

        if (targets.isEmpty()) return;

        PlayerEntity target = null;
        double minDist = Double.MAX_VALUE;
        for (PlayerEntity p : targets) {
            double d = p.distanceToSqr(player);
            if (d < minDist) { minDist = d; target = p; }
        }

        if (target != null) {
            float damage = (float) player.getAttributeValue(
                net.minecraft.entity.ai.attributes.Attributes.ATTACK_DAMAGE
            );
            target.hurt(DamageSource.playerAttack(player), damage);
        }
    }
}
