package com.degenerate_human.pigmen_forgiveness.mixin;

import com.degenerate_human.pigmen_forgiveness.PigmenForgiveness;
import com.degenerate_human.pigmen_forgiveness.events.AngryAtPlayerEvent;
import com.degenerate_human.pigmen_forgiveness.interfaces.ICanForgive;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.UUID;

@Mixin(EntityPigZombie.class)
public abstract class MixinEntityPigZombie extends Entity implements ICanForgive {

    @Shadow
    private int angerLevel;

    @Shadow
    private int randomSoundDelay;

    @Shadow
    private UUID angerTargetUUID;

    @Shadow
    public abstract void setRevengeTarget(@Nullable EntityLivingBase livingBase);

    public MixinEntityPigZombie(World worldIn) {
        super(worldIn);
    }

    public void becomeUnangeryBoi() {
        this.angerLevel = 0;
        this.randomSoundDelay = 0;
        this.angerTargetUUID = null;
        this.setRevengeTarget(null);
    }

    @Inject(at = @At("HEAD"), method = "becomeAngryAt")
    private void becomeAngryAt(Entity p_70835_1_, CallbackInfo callbackInfo) {
        if (p_70835_1_ instanceof EntityPlayer) {
            MinecraftForge.EVENT_BUS.post(new AngryAtPlayerEvent(this.getEntityId(), p_70835_1_.getUniqueID()));
            PigmenForgiveness.LOGGER.info("One angery boi");
            p_70835_1_.sendMessage(new TextComponentString("One angery boi"));
        }
    }
}
