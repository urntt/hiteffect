package com.urntt.hiteffect.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.urntt.hiteffect.effect.HitParticles;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Skips the server's hit particle animation when this mod already showed that particle for the local player's attack
 * on the same entity, so no hit shows it twice. Runs on the client thread, after vanilla's thread check.
 */
@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
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
