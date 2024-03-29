package com.fantasticsource.faerunutils.animations;

import com.fantasticsource.mctools.animation.CBipedAnimation;
import com.fantasticsource.tools.component.path.CPathConstant;
import com.fantasticsource.tools.component.path.CPathSinuous;
import com.fantasticsource.tools.datastructures.VectorN;
import net.minecraft.entity.Entity;

public class AniReverseUpwardsSwing1H extends CFaerunAnimation
{
    public AniReverseUpwardsSwing1H()
    {
        hitTime = 0.5;
        pauseAt = 500;
    }

    @Override
    public AniReverseUpwardsSwing1H start(Entity entity, boolean mainhand)
    {
        if (mainhand)
        {
            rightArm.zRotPath.path = new CPathConstant(new VectorN(Math.PI * -0.25));
            rightArm.yRotPath.path = new CPathConstant(new VectorN(0));
            rightArm.xRotPath.path = new CPathConstant(new VectorN(Math.PI * 1.75)).add(
                    new CPathSinuous(new CPathConstant(new VectorN(0.5)), 1, -0.25)
                            .add(new CPathConstant(new VectorN(0.5)))
                            .power(new CPathConstant(new VectorN(4 / (handItemSwap.rate * handItemSwap.rate))))
                            .mult(new CPathConstant(new VectorN(-Math.PI)))
            );

            rightItem.zRotPath.path = new CPathConstant(new VectorN(Math.PI));
            rightItem.xRotPath.path = new CPathConstant(new VectorN(Math.PI * -0.15)).add(
                    new CPathSinuous(new CPathConstant(new VectorN(0.5)), 1, -0.25)
                            .add(new CPathConstant(new VectorN(0.5)))
                            .power(new CPathConstant(new VectorN(4 / (handItemSwap.rate * handItemSwap.rate))))
                            .mult(new CPathConstant(new VectorN(Math.PI * -0.6)))
            );
        }
        else
        {
            leftArm.zRotPath.path = new CPathConstant(new VectorN(Math.PI * 0.25));
            leftArm.yRotPath.path = new CPathConstant(new VectorN(0));
            leftArm.xRotPath.path = new CPathConstant(new VectorN(Math.PI * 1.75)).add(
                    new CPathSinuous(new CPathConstant(new VectorN(0.5)), 1, -0.25)
                            .add(new CPathConstant(new VectorN(0.5)))
                            .power(new CPathConstant(new VectorN(4 / (handItemSwap.rate * handItemSwap.rate))))
                            .mult(new CPathConstant(new VectorN(-Math.PI)))
            );

            leftItem.zRotPath.path = new CPathConstant(new VectorN(Math.PI));
            leftItem.xRotPath.path = new CPathConstant(new VectorN(Math.PI * -0.15)).add(
                    new CPathSinuous(new CPathConstant(new VectorN(0.5)), 1, -0.25)
                            .add(new CPathConstant(new VectorN(0.5)))
                            .power(new CPathConstant(new VectorN(4 / (handItemSwap.rate * handItemSwap.rate))))
                            .mult(new CPathConstant(new VectorN(Math.PI * -0.6)))
            );
        }

        setAllStartTimes(System.currentTimeMillis());
        CBipedAnimation.addAnimation(entity, this);

        return this;
    }
}
