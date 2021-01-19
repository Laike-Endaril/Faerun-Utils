package com.fantasticsource.faerunutils.interaction.trading;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.HashMap;

public class Trading
{
    public static final HashMap<EntityPlayerMP, TradeData> TRADE_DATA = new HashMap<>();

    public static void tryStart(EntityPlayerMP p1, EntityPlayerMP p2)
    {
        if (p1 == p2) p1.sendMessage(new TextComponentString(TextFormatting.RED + "You attempt to trade with yourself, but fail"));
        else if (p1.dimension != 0) p1.sendMessage(new TextComponentString(TextFormatting.RED + "You can only trade in town"));
        else if (p2 == null) p1.sendMessage(new TextComponentString(TextFormatting.RED + "They seem to have vanished"));
        else if (p2.world != p1.world || p2.getDistanceSq(p1) > 16) p1.sendMessage(new TextComponentString(TextFormatting.RED + p2.getName() + " is too far away to trade"));
        else
        {
            TradeData data2 = TRADE_DATA.get(p2);

            if (data2 == null)
            {
                TRADE_DATA.put(p1, new TradeData(p1, p2));
                p1.sendMessage(new TextComponentString(TextFormatting.AQUA + "Requested a trade with " + p2.getName()));
                p2.sendMessage(new TextComponentString(TextFormatting.AQUA + p1.getName() + " requests a trade"));
            }
            else
            {
                EntityPlayerMP p3 = data2.playerBesides(p2);

                if (p3 == p1)
                {
                    TRADE_DATA.put(p1, data2);
                    start(data2);
                }
                else if (TRADE_DATA.containsKey(p3) && TRADE_DATA.get(p3).playerBesides(p3) == p2)
                {
                    p1.sendMessage(new TextComponentString(TextFormatting.AQUA + p2.getName() + " is busy right now"));
                }
                else
                {
                    TRADE_DATA.put(p1, new TradeData(p1, p2));
                    p1.sendMessage(new TextComponentString(TextFormatting.AQUA + "Requested a trade with " + p2.getName()));
                    p2.sendMessage(new TextComponentString(TextFormatting.AQUA + p1.getName() + " requests a trade"));
                }
            }
        }
    }

    protected static void start(TradeData data)
    {
        //TODO open client GUIs and server container
    }


    public static void tryLock(EntityPlayerMP p1, boolean lock)
    {
        TradeData data = TRADE_DATA.get(p1);
        if (data == null)
        {
            p1.sendMessage(new TextComponentString(TextFormatting.RED + "Something went wrong; closing trade GUI"));
            //TODO close GUI
        }
        else
        {
            EntityPlayerMP p2 = data.playerBesides(p1);

            if (p1 == p2)
            {
                p1.sendMessage(new TextComponentString(TextFormatting.RED + "You attempt to trade with yourself, but fail"));
                //TODO close GUI
            }
            else if (p1.dimension != 0)
            {
                p1.sendMessage(new TextComponentString(TextFormatting.RED + "You can only trade in town"));
                //TODO close GUI
            }
            else if (p2 == null)
            {
                p1.sendMessage(new TextComponentString(TextFormatting.RED + "They seem to have vanished"));
                //TODO close GUI
            }
            else if (p2.world != p1.world || p2.getDistanceSq(p1) > 16)
            {
                p1.sendMessage(new TextComponentString(TextFormatting.RED + p2.getName() + " is too far away to trade"));
                //TODO close GUI
            }
            else
            {
                if (p1 == data.p1) data.p1Locked = lock;
                else data.p2Locked = lock;
                data.p1Ready = false;
                data.p2Ready = false;
                data.sendUpdates();
            }
        }
    }


    public static void tryComplete(EntityPlayerMP p1, boolean complete)
    {
        TradeData data = TRADE_DATA.get(p1);
        if (data == null)
        {
            p1.sendMessage(new TextComponentString(TextFormatting.RED + "Something went wrong; closing trade GUI"));
            //TODO close GUI
        }
        else
        {
            EntityPlayerMP p2 = data.playerBesides(p1);

            if (p1 == p2)
            {
                p1.sendMessage(new TextComponentString(TextFormatting.RED + "You attempt to trade with yourself, but fail"));
                //TODO close GUI
            }
            else if (p1.dimension != 0)
            {
                p1.sendMessage(new TextComponentString(TextFormatting.RED + "You can only trade in town"));
                //TODO close GUI
            }
            else if (p2 == null)
            {
                p1.sendMessage(new TextComponentString(TextFormatting.RED + "They seem to have vanished"));
                //TODO close GUI
            }
            else if (p2.world != p1.world || p2.getDistanceSq(p1) > 16)
            {
                p1.sendMessage(new TextComponentString(TextFormatting.RED + p2.getName() + " is too far away to trade"));
                //TODO close GUI
            }
            else if (data.p1Locked && data.p2Locked)
            {
                if (p1 == data.p1) data.p1Ready = complete;
                else data.p2Ready = complete;
                if (data.p1Ready && data.p2Ready) complete(data);
                else data.sendUpdates();
            }
            else
            {
                data.p1Ready = false;
                data.p2Ready = false;
                data.sendUpdates();
            }
        }
    }

    protected static void complete(TradeData data)
    {
        //TODO MCTools.give() all items to respective players

        data.p1Locked = false;
        data.p2Locked = false;
        data.p1Ready = false;
        data.p2Ready = false;
    }


    public static class TradeData
    {
        public final EntityPlayerMP p1, p2;
        public boolean p1Locked = false, p2Locked = false, p1Ready = false, p2Ready = false;

        public TradeData(EntityPlayerMP p1, EntityPlayerMP p2)
        {
            this.p1 = p1;
            this.p2 = p2;
        }

        public boolean hasPlayer(EntityPlayerMP player)
        {
            return player == p1 || player == p2;
        }

        public EntityPlayerMP playerBesides(EntityPlayerMP player)
        {
            if (player == p1) return p2;
            if (player == p2) return p1;
            throw new IllegalArgumentException("Player must be one of the players involved");
        }

        public void sendUpdates()
        {
            //TODO update GUIs for both players
        }
    }
}
