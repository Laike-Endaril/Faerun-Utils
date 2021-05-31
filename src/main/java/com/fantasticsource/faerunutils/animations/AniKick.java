package com.fantasticsource.faerunutils.animations;

import com.fantasticsource.mctools.animation.CBipedAnimation;
import com.fantasticsource.tools.component.path.CPathConstant;
import com.fantasticsource.tools.component.path.CPathSinuous;
import com.fantasticsource.tools.datastructures.VectorN;
import net.minecraft.entity.Entity;

public class AniKick extends CFaerunAnimation
{
    public AniKick()
    {
        hitTime = 0.5;
        removeAt = 1000;
    }

    @Override
    public AniKick start(Entity entity, boolean mainhand)
    {
        rightLeg.zRotPath.path = new CPathConstant(new VectorN(Math.PI * 0.15));
        rightLeg.yRotPath.path = new CPathSinuous(new CPathConstant(new VectorN(0.5)), 1, -0.25)
                .add(new CPathConstant(new VectorN(0.5)))
                .power(new CPathConstant(new VectorN(8)))
                .mult(new CPathConstant(new VectorN(Math.PI * -0.05)));
        rightLeg.xRotPath.path = new CPathConstant(new VectorN(Math.PI * 0.25)).add(
                new CPathSinuous(new CPathConstant(new VectorN(0.5)), 1, -0.25)
                        .add(new CPathConstant(new VectorN(0.5)))
                        .power(new CPathConstant(new VectorN(8)))
                        .mult(new CPathConstant(new VectorN(Math.PI * -0.75)))
        );

        leftLeg.zRotPath.path = new CPathConstant(new VectorN(0));
        leftLeg.yRotPath.path = new CPathConstant(new VectorN(0));
        leftLeg.xRotPath.path = new CPathConstant(new VectorN(0));

        setAllStartTimes(System.currentTimeMillis());
        CBipedAnimation.addAnimation(entity, this);

        return this;
    }
}
