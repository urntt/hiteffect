package com.urntt.hiteffect.gametest.mixin;

import com.urntt.hiteffect.gametest.ParticleCounter;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Counts the hit particle emitters the game creates, whether vanilla or the mod creates them.
 */
@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin {
	@Inject(
			method = "createTrackingEmitter(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/particles/ParticleOptions;)V",
			at = @At("HEAD"))
	private void hiteffectGametest$count(final Entity entity, final ParticleOptions particle, final CallbackInfo ci) {
		ParticleCounter.record(particle);
	}
}
