package com.chainbreak;

import com.chainbreak.net.ImpactPacket;
import com.chainbreak.net.Network;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.DodgeAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKeys;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** The actual "Chain Break" behaviour (server side). */
public final class RollLogic {
    /** Game time at which each player activated the roll (covers the 1-2 ticks before the animation is applied). */
    private static final Map<UUID, Long> ACTIVATED_AT = new HashMap<>();

    public static void tryActivate(ServerPlayer player, boolean backward, float yaw) {
        ServerPlayerPatch patch = EpicFightCapabilities.getServerPlayerPatch(player);
        if (patch == null) return;

        if (!player.isAlive() || player.isSpectator() || player.isPassenger()
                || player.isInWater() || player.onClimbable() || !player.onGround()) {
            return;
        }

        // Gauge must be full.
        if (GaugeStore.get(player) < GaugeStore.max() - 0.001F) return;

        // Stunned / knocked down -> cannot use it, and an active roll is cancelled by the stun anyway.
        EntityState state = patch.getEntityState();
        if (state.hurt() || state.knockDown()) return;

        // Already rolling.
        if (isRolling(player)) return;

        GaugeStore.set(player, 0.0F);
        GaugeStore.sync(player);

        // 1) Drop the combo immediately.
        resetCombo(patch, player);

        // 2) Play Epic Fight's own roll animation. Starting it with 0 transition time
        //    replaces whatever attack animation was playing, which is what breaks the combo.
        AnimationAccessor<? extends DodgeAnimation> roll =
                backward ? Animations.BIPED_ROLL_BACKWARD : Animations.BIPED_ROLL_FORWARD;
        patch.playAnimationSynchronized(roll, 0.0F);
        patch.setModelYRot(yaw, true);

        ACTIVATED_AT.put(player.getUUID(), player.level().getGameTime());

        // 3) Screen impact on the owner's client.
        Network.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ImpactPacket());
    }

    private static void resetCombo(ServerPlayerPatch patch, ServerPlayer player) {
        SkillContainer basic = patch.getSkillCapability().getSkillContainerFor(SkillSlots.BASIC_ATTACK);
        if (basic != null) {
            basic.getDataManager().setDataSync(SkillDataKeys.COMBO_COUNTER.get(), 0);
        }
    }

    /**
     * True while the player is inside the roll. The roll ends the moment Epic Fight swaps the
     * animation (stun / knockdown / hit animation), so being stunned removes the invulnerability instantly.
     */
    public static boolean isRolling(ServerPlayer player) {
        ServerPlayerPatch patch = EpicFightCapabilities.getServerPlayerPatch(player);
        if (patch == null) return false;

        EntityState state = patch.getEntityState();
        if (state.hurt() || state.knockDown()) {
            ACTIVATED_AT.remove(player.getUUID());
            return false;
        }

        Long at = ACTIVATED_AT.get(player.getUUID());
        if (at != null && player.level().getGameTime() - at <= 2L) {
            return true;
        }

        AnimationPlayer animPlayer = patch.getAnimator().getPlayerFor(null);
        if (animPlayer == null) return false;

        DynamicAnimation current = animPlayer.getAnimation().get();
        return current == Animations.BIPED_ROLL_FORWARD.get() || current == Animations.BIPED_ROLL_BACKWARD.get();
    }

    public static void forget(ServerPlayer player) {
        ACTIVATED_AT.remove(player.getUUID());
    }

    private RollLogic() {}
}
