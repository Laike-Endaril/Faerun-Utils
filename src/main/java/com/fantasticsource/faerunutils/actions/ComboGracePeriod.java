package com.fantasticsource.faerunutils.actions;

import com.fantasticsource.tools.component.CBoolean;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;

public class ComboGracePeriod extends Cooldown
{
    public CFaerunAction previousAction;


    public ComboGracePeriod()
    {
        this(null, 0);
    }

    public ComboGracePeriod(CFaerunAction previousAction, double time)
    {
        super(time);
        this.previousAction = previousAction;
    }


    @Override
    protected void doStuff()
    {
        new Cooldown(1.5).queue(source, queue.name);
    }


    @Override
    public ComboGracePeriod write(ByteBuf buf)
    {
        super.write(buf);

        buf.writeBoolean(previousAction != null);
        if (previousAction != null) writeMarked(buf, previousAction);

        return this;
    }

    @Override
    public ComboGracePeriod read(ByteBuf buf)
    {
        super.read(buf);

        if (buf.readBoolean()) previousAction = (CFaerunAction) readMarked(buf);
        else previousAction = null;

        return this;
    }

    @Override
    public ComboGracePeriod save(OutputStream stream)
    {
        super.save(stream);

        new CBoolean().set(previousAction != null).save(stream);
        if (previousAction != null) saveMarked(stream, previousAction);

        return this;
    }

    @Override
    public ComboGracePeriod load(InputStream stream)
    {
        super.load(stream);

        if (new CBoolean().load(stream).value) previousAction = (CFaerunAction) loadMarked(stream);
        else previousAction = null;

        return this;
    }
}
