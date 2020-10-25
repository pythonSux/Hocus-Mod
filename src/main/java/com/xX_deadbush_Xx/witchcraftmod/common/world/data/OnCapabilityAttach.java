package com.xX_deadbush_Xx.witchcraftmod.common.world.data;

import com.xX_deadbush_Xx.witchcraftmod.WitchcraftMod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WitchcraftMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OnCapabilityAttach {

    @SubscribeEvent
    public static void attachToPlayer(AttachCapabilitiesEvent<Entity> event) {
    	Entity entity = event.getObject();
    	if(entity instanceof PlayerEntity) {
    		 event.addCapability(new ResourceLocation(WitchcraftMod.MOD_ID, "player_crystal_energy"), new PlayerManaProvider());
    	     event.addCapability(new ResourceLocation(WitchcraftMod.MOD_ID, "player_spell"), new PlayerSpellProvider((PlayerEntity)entity));
    	}
    }
}