package com.xX_deadbush_Xx.hocus.common.spell;

import com.xX_deadbush_Xx.hocus.api.spell.ISpellRenderer;
import com.xX_deadbush_Xx.hocus.api.spell.SpellCast;
import com.xX_deadbush_Xx.hocus.client.renderers.spell_renderers.ManawaveSpellRenderer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class DestructionSpell extends SpellCast {

	public DestructionSpell(PlayerEntity player, ItemStack wand, int... args) {
		super(player, wand, args);
	}

	public DestructionSpell(int ticks, int... args) {
		super(ticks, args);
	}

	@Override
	public ISpellRenderer<? extends SpellCast> getRenderer() {
		return ManawaveSpellRenderer.INSTANCE;
	}

	
}