package com.urntt.hiteffect.config;

/**
 * The values every effect reads. Implemented by the mod's own configuration, by the OneConfig integration and by
 * the constant {@link EffectValues} sets.
 */
public interface EffectSettings {
	/** Whether armor and other equipment layers flash with the hurt entity. */
	boolean armorFlash();

	/** Whether held items and worn head items flash with the hurt entity. */
	boolean itemFlash();

	/** Whether the first-person arm flashes while the local player is hurt. */
	boolean firstPersonArmFlash();

	/** The hurt flash color as {@code 0xRRGGBB}. Ignored while {@link #rainbow()} is on. */
	int hurtColor();

	/** How strongly the hurt flash tints the entity, from {@code 0} to {@link HurtColors#MAX_STRENGTH} percent. */
	int hurtStrength();

	/** Whether the hurt flash cycles through all hues instead of using {@link #hurtColor()}. */
	boolean rainbow();

	/** Whether every melee hit of the local player shows sharpness (enchanted hit) particles. */
	boolean sharpnessParticles();

	/** Whether every melee hit of the local player shows critical hit particles. */
	boolean critParticles();
}
