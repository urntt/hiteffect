package com.urntt.hiteffect.mixin;

import com.urntt.hiteffect.effect.HitParticles;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Remembers which entity the local player attacked, so that {@link HitParticles} can recognize the server's
 * confirmation of the hit. The attack itself stays vanilla.
 */
@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
	@Inject(method = "attack", at = @At("HEAD"))
	private void hiteffect$rememberTarget(final Player player, final Entity entity, final CallbackInfo ci) {
		HitParticles.onAttack(entity);
	}
}
