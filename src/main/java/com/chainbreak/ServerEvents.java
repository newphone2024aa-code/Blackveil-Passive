package com.chainbreak;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ServerEvents {

    /** Every hit that actually damages something fills the gauge. */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDamageDealt(LivingDamageEvent event) {
        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof ServerPlayer attacker)) return;
        if (event.getEntity() == attacker) return;
        if (event.getAmount() <= 0.0F) return;
        if (source.is(DamageTypes.THORNS)) return;
        if (RollLogic.isRolling(attacker)) return;

        if (GaugeStore.add(attacker, ChainConfig.GAIN_PER_HIT.get().floatValue())) {
            GaugeStore.sync(attacker);
        }
    }

    /** No damage while rolling. HIGHEST so we cancel before Epic Fight reacts (stun/knockback). */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onAttacked(LivingAttackEvent event) {
        if (!ChainConfig.TOTAL_INVULNERABLE.get()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return; // void, /kill
        if (RollLogic.isRolling(player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onKnockBack(LivingKnockBackEvent event) {
        if (!ChainConfig.TOTAL_INVULNERABLE.get()) return;
        if (event.getEntity() instanceof ServerPlayer player && RollLogic.isRolling(player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) GaugeStore.sync(player);
    }

    @SubscribeEvent
    public void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) GaugeStore.sync(player);
    }

    @SubscribeEvent
    public void onDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) GaugeStore.sync(player);
    }

    /** Keep the gauge across dimension changes, reset it on death. */
    @SubscribeEvent
    public void onClone(PlayerEvent.Clone event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        float value = event.isWasDeath() ? 0.0F : GaugeStore.get((ServerPlayer) event.getOriginal());
        GaugeStore.set(player, value);
    }

    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) RollLogic.forget(player);
    }
}
