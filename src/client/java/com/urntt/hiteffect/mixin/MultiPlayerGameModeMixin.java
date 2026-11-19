package com.urntt.hiteffect.mixin;

import com.urntt.hiteffect.effect.HitParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Shows hit particles the moment the local player attacks, without waiting for the server. The attack itself, and
 * the packet vanilla sends for it, stay vanilla; this mod sends nothing.
 */
@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(method = "attack", at = @At("TAIL"))
	private void hiteffect$showHitParticles(final Player player, final Entity entity, final CallbackInfo ci) {
		HitParticles.onAttack(this.minecraft, player, entity);
	}
}
