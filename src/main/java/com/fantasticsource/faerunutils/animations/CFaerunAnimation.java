package com.fantasticsource.faerunutils.animations;

import com.fantasticsource.mctools.animation.CBipedAnimation;
import net.minecraft.entity.Entity;

public abstract class CFaerunAnimation extends CBipedAnimation
{
    public double hitTime = 0; //Only used for calculating animation rate server-side at the start of animation

    public CFaerunAnimation()
    {
        bodyFacesLookDirection = true;
    }

    public abstract CFaerunAnimation start(Entity entity, boolean mainhand);

    @Override
    public CFaerunAnimation copy()
    {
        return (CFaerunAnimation) super.copy();
    }
}
