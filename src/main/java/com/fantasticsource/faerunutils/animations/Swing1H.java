package com.fantasticsource.faerunutils.animations;

import com.fantasticsource.tools.component.path.CPathConstant;
import com.fantasticsource.tools.component.path.CPathSinuous;
import com.fantasticsource.tools.datastructures.VectorN;
import net.minecraft.entity.Entity;

public class Swing1H extends CFaerunAnimation
{
    public static final Swing1H INSTANCE = new Swing1H();

    public Swing1H()
    {
        duration = 500;
    }

    @Override
    public void start(Entity entity, boolean right)
    {
        if (right)
        {
            rightArm.zRotPath.path = new CPathConstant(new VectorN(Math.PI * 0.25));
            rightArm.yRotPath.path = new CPathConstant(new VectorN(Math.PI));
            rightArm.xRotPath.path = new CPathConstant(new VectorN(-Math.PI * 0.25)).add(
                    new CPathSinuous(new CPathConstant(new VectorN(0.5)), 1, -0.25)
                            .add(new CPathConstant(new VectorN(0.5)))
                            .power(new CPathConstant(new VectorN(2)))
                            .mult(new CPathConstant(new VectorN(Math.PI)))
            );
        }
        else
        {
            leftArm.zRotPath.path = new CPathConstant(new VectorN(-Math.PI * 0.25));
            leftArm.yRotPath.path = new CPathConstant(new VectorN(Math.PI));
            leftArm.xRotPath.path = new CPathConstant(new VectorN(-Math.PI * 0.25)).add(
                    new CPathSinuous(new CPathConstant(new VectorN(0.5)), 1, -0.25)
                            .add(new CPathConstant(new VectorN(0.5)))
                            .power(new CPathConstant(new VectorN(2)))
                            .mult(new CPathConstant(new VectorN(Math.PI)))
            );
        }
    }
}
