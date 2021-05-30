package com.fantasticsource.faerunutils.animations;

import com.fantasticsource.mctools.animation.CBipedAnimation;
import com.fantasticsource.tools.component.path.CPathConstant;
import com.fantasticsource.tools.component.path.CPathSinuous;
import com.fantasticsource.tools.datastructures.VectorN;
import net.minecraft.entity.Entity;

public class Thrust1H extends CFaerunAnimation
{
    public Thrust1H()
    {
        hitTime = 0.5;
        pauseAt = 1000;
    }

    @Override
    public Thrust1H start(Entity entity, boolean mainhand)
    {
        if (mainhand)
        {
            rightArm.zRotPath.path = new CPathConstant(new VectorN(Math.PI * 0.05));
            rightArm.yRotPath.path = new CPathSinuous(new CPathConstant(new VectorN(0.5)), 1, -0.25)
                    .add(new CPathConstant(new VectorN(0.5)))
                    .power(new CPathConstant(new VectorN(8)))
                    .mult(new CPathConstant(new VectorN(Math.PI * -0.05)));
            rightArm.xRotPath.path = new CPathConstant(new VectorN(Math.PI * 0.25)).add(
                    new CPathSinuous(new CPathConstant(new VectorN(0.5)), 1, -0.25)
                            .add(new CPathConstant(new VectorN(0.5)))
                            .power(new CPathConstant(new VectorN(8)))
                            .mult(new CPathConstant(new VectorN(Math.PI * -0.75)))
            );

            rightItem.xRotPath.path = new CPathConstant(new VectorN(Math.PI * 0.35)).add(
                    new CPathSinuous(new CPathConstant(new VectorN(0.5)), 1, -0.25)
                            .add(new CPathConstant(new VectorN(0.5)))
                            .power(new CPathConstant(new VectorN(8)))
                            .mult(new CPathConstant(new VectorN(Math.PI * -0.85)))
            );
        }
        else
        {
            leftArm.zRotPath.path = new CPathConstant(new VectorN(Math.PI * -0.05));
            leftArm.yRotPath.path = new CPathSinuous(new CPathConstant(new VectorN(0.5)), 1, -0.25)
                    .add(new CPathConstant(new VectorN(0.5)))
                    .power(new CPathConstant(new VectorN(8)))
                    .mult(new CPathConstant(new VectorN(Math.PI * -0.05)));
            leftArm.xRotPath.path = new CPathConstant(new VectorN(Math.PI * 0.25)).add(
                    new CPathSinuous(new CPathConstant(new VectorN(0.5)), 1, -0.25)
                            .add(new CPathConstant(new VectorN(0.5)))
                            .power(new CPathConstant(new VectorN(8)))
                            .mult(new CPathConstant(new VectorN(Math.PI * -0.75)))
            );

            leftItem.xRotPath.path = new CPathConstant(new VectorN(Math.PI * 0.35)).add(
                    new CPathSinuous(new CPathConstant(new VectorN(0.5)), 1, -0.25)
                            .add(new CPathConstant(new VectorN(0.5)))
                            .power(new CPathConstant(new VectorN(8)))
                            .mult(new CPathConstant(new VectorN(Math.PI * -0.85)))
            );
        }

        setAllStartTimes(System.currentTimeMillis());
        CBipedAnimation.addAnimation(entity, this);

        return this;
    }
}
