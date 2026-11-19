package com.urntt.hiteffect.gui;

import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

/**
 * A slider over a closed integer range that reports every change, and whose value can also be set from code without
 * reporting it.
 */
public final class IntSlider extends AbstractSliderButton {
	private final int min;
	private final int max;
	private final IntFunction<Component> message;
	private final IntConsumer listener;

	/**
	 * @param message  builds the slider's text from its value
	 * @param listener called with the new value whenever the player changes it
	 */
	public IntSlider(final int min, final int max, final int initialValue, final IntFunction<Component> message, final IntConsumer listener) {
		super(0, 0, 150, DEFAULT_HEIGHT, message.apply(initialValue), toSliderValue(min, max, initialValue));
		this.min = min;
		this.max = max;
		this.message = message;
		this.listener = listener;
	}

	public int getIntValue() {
		return this.min + (int) Math.round(this.value * (this.max - this.min));
	}

	/** Moves the slider to {@code value} without notifying the listener. */
	public void setIntValue(final int value) {
		this.value = toSliderValue(this.min, this.max, value);
		this.updateMessage();
	}

	@Override
	protected void updateMessage() {
		this.setMessage(this.message.apply(this.getIntValue()));
	}

	@Override
	protected void applyValue() {
		this.listener.accept(this.getIntValue());
	}

	private static double toSliderValue(final int min, final int max, final int value) {
		return (Mth.clamp(value, min, max) - min) / (double) (max - min);
	}
}
