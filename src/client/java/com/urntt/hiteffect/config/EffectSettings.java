package com.urntt.hiteffect.config;

/**
 * The values every effect reads. Implemented by the mod's own configuration, by the OneConfig integration and by
 * the constant {@link EffectValues} sets.
 */
public interface EffectSettings {
	/** Whether {@code part} flashes with the hurt entity, or for the first-person arm, while the player is hurt. */
	boolean flashes(FlashPart part);

	/** The hurt flash color as {@code 0xRRGGBB}. Ignored while {@link #rainbow()} is on. */
	int hurtColor();

	/** How strongly the hurt flash tints the entity, from {@code 0} to {@link HurtColors#MAX_STRENGTH} percent. */
	int hurtStrength();

	/** Whether the hurt flash cycles through all hues instead of using {@link #hurtColor()}. */
	boolean rainbow();

	/** Whether every melee attack of the local player shows sharpness (enchanted hit) particles. */
	boolean sharpnessParticles();

	/** Whether every melee attack of the local player shows critical hit particles. */
	boolean critParticles();
}
