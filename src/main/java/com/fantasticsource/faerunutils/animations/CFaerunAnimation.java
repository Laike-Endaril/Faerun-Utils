package com.fantasticsource.faerunutils.animations;

import com.fantasticsource.mctools.animation.CBipedAnimation;
import net.minecraft.entity.Entity;

public abstract class CFaerunAnimation extends CBipedAnimation
{
    public abstract void start(Entity entity, boolean right);

    @Override
    public CFaerunAnimation copy()
    {
        return (CFaerunAnimation) super.copy();
    }
}
