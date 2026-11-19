package com.urntt.hiteffect.mixin;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Exposes the setup and name of a {@link RenderType}, so that it can be duplicated with another pipeline.
 */
@Mixin(RenderType.class)
public interface RenderTypeAccessor {
	@Accessor("state")
	RenderSetup hiteffect$getState();

	@Accessor("name")
	String hiteffect$getName();
}
