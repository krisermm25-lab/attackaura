package com.example.attackaura;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class AttackAuraHandler {

    private static final double RADIUS = 5.0;
    private static final int    DELAY  = 10;

    private int tick = 0;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
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
