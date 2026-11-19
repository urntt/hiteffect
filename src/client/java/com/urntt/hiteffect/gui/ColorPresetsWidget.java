package com.urntt.hiteffect.gui;

import java.util.function.IntConsumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/**
 * A row of preset color swatches. Clicking one picks its color.
 */
public final class ColorPresetsWidget extends AbstractWidget {
	private static final int GAP = 4;

	private final int[] colors;
	private final IntConsumer listener;

	/**
	 * @param colors   the preset colors, {@code 0xRRGGBB}
	 * @param listener called with the color of the clicked swatch
	 */
	public ColorPresetsWidget(final Component message, final int[] colors, final IntConsumer listener) {
		super(0, 0, 310, 20, message);
		this.colors = colors.clone();
		this.listener = listener;
	}

	@Override
	protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
		int hovered = this.isHovered() && this.active ? this.indexAt(mouseX) : -1;
		for (int i = 0; i < this.colors.length; i++) {
			ColorSwatchWidget.drawSwatch(
					graphics, this.swatchX(i), this.getY(), this.swatchWidth(), this.getHeight(), this.colors[i], this.active, i == hovered);
		}
		this.handleCursor(graphics);
	}

	@Override
	public void onClick(final MouseButtonEvent event, final boolean doubleClick) {
		int index = this.indexAt(event.x());
		if (index >= 0) {
			this.listener.accept(this.colors[index]);
		}
	}

	@Override
	protected void updateWidgetNarration(final NarrationElementOutput output) {
		output.add(NarratedElementType.TITLE, this.getMessage());
	}

	private int swatchWidth() {
		return (this.getWidth() - GAP * (this.colors.length - 1)) / this.colors.length;
	}

	private int swatchX(final int index) {
		return this.getX() + index * (this.swatchWidth() + GAP);
	}

	private int indexAt(final double mouseX) {
		for (int i = 0; i < this.colors.length; i++) {
			int x = this.swatchX(i);
			if (mouseX >= x && mouseX < x + this.swatchWidth()) {
				return i;
			}
		}
		return -1;
	}
}
