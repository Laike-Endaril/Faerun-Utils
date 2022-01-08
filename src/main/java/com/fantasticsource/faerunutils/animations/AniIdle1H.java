package com.fantasticsource.faerunutils.animations;

import com.fantasticsource.mctools.animation.CBipedAnimation;
import com.fantasticsource.tools.component.path.CPathConstant;
import com.fantasticsource.tools.datastructures.VectorN;
import net.minecraft.entity.Entity;

public class AniIdle1H extends CFaerunAnimation
{
    @Override
    public AniIdle1H start(Entity entity, boolean mainhand)
    {
        if (mainhand)
        {
            rightArm.zRotPath.path = new CPathConstant(new VectorN(Math.PI * 0.15));
            rightArm.yRotPath.path = new CPathConstant(new VectorN(Math.PI));
            rightArm.xRotPath.path = new CPathConstant(new VectorN(-Math.PI * 0.15));
        }
        else
        {
            leftArm.zRotPath.path = new CPathConstant(new VectorN(-Math.PI * 0.15));
            leftArm.yRotPath.path = new CPathConstant(new VectorN(Math.PI));
            leftArm.xRotPath.path = new CPathConstant(new VectorN(-Math.PI * 0.15));
        }

        CBipedAnimation.addAnimation(entity, this);

        return this;
    }
}
