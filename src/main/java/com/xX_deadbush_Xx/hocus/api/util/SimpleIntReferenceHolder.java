package com.xX_deadbush_Xx.hocus.api.util;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

import net.minecraft.util.IntReferenceHolder;

public class SimpleIntReferenceHolder extends IntReferenceHolder {

    private final IntSupplier getter;
    private final IntConsumer setter;

    public SimpleIntReferenceHolder(final IntSupplier getter, final IntConsumer setter) {
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public int get() {
        return this.getter.getAsInt();
    }

    @Override
    public void set(final int newValue) {
        this.setter.accept(newValue);
    }

}
