package com.fantasticsource.faerunutils.actions;

import com.fantasticsource.tools.component.CDouble;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;

public class Cooldown extends CFaerunAction
{
    public Cooldown()
    {
    }

    public Cooldown(double time)
    {
        useTime = time;
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
