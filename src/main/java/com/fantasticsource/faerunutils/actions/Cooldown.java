package com.fantasticsource.faerunutils.actions;

import com.fantasticsource.faerunutils.Attributes;
import com.fantasticsource.tools.component.CDouble;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

import java.io.InputStream;
import java.io.OutputStream;

public class Cooldown extends CFaerunAction
{
    public Cooldown()
    {
        this(0);
    }

    public Cooldown(double time)
    {
        super("faerunutils.cooldown");
        useTime = time;
    }


    @Override
    protected void execute(Entity source, String event)
    {
        super.execute(source, event);
        if (getClass() == Cooldown.class)
        {
            if (event.equals("end"))
            {
                Attributes.COMBO.setCurrentAmount(source, Attributes.COMBO.getTotalAmount(source));
                USED_COMBO_ANIMATIONS.remove(source);
            }
        }
    }


    @Override
    protected void onCompletion()
    {
    }


    @Override
    public Cooldown write(ByteBuf buf)
    {
        super.write(buf);

        buf.writeDouble(useTime);

        return this;
    }

    @Override
    public Cooldown read(ByteBuf buf)
    {
        super.read(buf);

        useTime = buf.readDouble();

        return this;
    }

    @Override
    public Cooldown save(OutputStream stream)
    {
        super.save(stream);

        new CDouble().set(useTime).save(stream);

        return this;
    }

    @Override
    public Cooldown load(InputStream stream)
    {
        super.load(stream);

        useTime = new CDouble().load(stream).value;

        return this;
    }
}
