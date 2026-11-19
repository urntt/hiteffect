package com.urntt.hiteffect.mixin;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import java.util.Map;
import net.minecraft.client.renderer.oit.OitPipelineSet;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Reads a {@link RenderSetup} and creates a copy of it, so that a render type can be duplicated with another pipeline
 * without restating vanilla's settings.
 */
@Mixin(RenderSetup.class)
public interface RenderSetupAccessor {
	@Invoker("<init>")
	static RenderSetup hiteffect$create(
			final RenderPipeline pipeline,
			final @Nullable OitPipelineSet oitPipelineSet,
			final Map<String, ?> textures,
			final boolean useLightmap,
			final boolean useOverlay,
			final LayeringTransform layeringTransform,
			final TextureTransform textureTransform,
			final RenderSetup.OutlineProperty outlineProperty,
			final @Nullable String outlineTextureName,
			final boolean affectsCrumbling,
			final boolean sortOnUpload,
			final boolean forceSolidModelPhase) {
		throw new AssertionError();
	}

	@Accessor("pipeline")
	RenderPipeline hiteffect$getPipeline();

	@Accessor("oitPipelineSet")
	@Nullable OitPipelineSet hiteffect$getOitPipelineSet();

	@Accessor("textures")
	Map<String, ?> hiteffect$getTextures();

	@Accessor("useLightmap")
	boolean hiteffect$useLightmap();

	@Accessor("useOverlay")
	boolean hiteffect$useOverlay();

	@Accessor("layeringTransform")
	LayeringTransform hiteffect$getLayeringTransform();

	@Accessor("textureTransform")
	TextureTransform hiteffect$getTextureTransform();

	@Accessor("outlineProperty")
	RenderSetup.OutlineProperty hiteffect$getOutlineProperty();

	@Accessor("outlineTextureName")
	@Nullable String hiteffect$getOutlineTextureName();

	@Accessor("affectsCrumbling")
	boolean hiteffect$affectsCrumbling();

	@Accessor("sortOnUpload")
	boolean hiteffect$sortOnUpload();

	@Accessor("forceSolidModelPhase")
	boolean hiteffect$forceSolidModelPhase();
}
