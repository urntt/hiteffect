package com.urntt.hiteffect.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.urntt.hiteffect.effect.HitParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Shows hit particles when the server confirms a melee hit of the local player, and skips vanilla's particle for that
 * hit when this mod already showed it. Both handlers run on the client thread after vanilla's thread check.
 */
@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
	@WrapOperation(
			method = "handleDamageEvent",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;handleDamageEvent(Lnet/minecraft/world/damagesource/DamageSource;)V"))
	private void hiteffect$showHitParticles(final Entity entity, final DamageSource source, final Operation<Void> original) {
		original.call(entity, source);
		HitParticles.onDamage(Minecraft.getInstance(), entity, source);
	}

	@WrapOperation(
			method = "handleAnimate",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/particle/ParticleEngine;createTrackingEmitter(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/particles/ParticleOptions;)V"))
	private void hiteffect$skipDuplicate(
			final ParticleEngine particleEngine, final Entity entity, final ParticleOptions particle, final Operation<Void> original) {
		if (!HitParticles.isDuplicate(entity, particle)) {
			original.call(particleEngine, entity, particle);
		}
	}
}
