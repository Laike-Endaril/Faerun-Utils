package com.fantasticsource.faerunutils;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.ServerTickTimer;
import com.fantasticsource.omnipotence.Debug;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static com.fantasticsource.faerunutils.FaerunUtilsConfig.gcMessageMode;

public class GCMessageFixer
{
    private static final int DELAY = 20;
    private static final String EXT = "gclog";

    private static BufferedReader reader;
    private static StringBuilder current = new StringBuilder();
    private static boolean omnipotence;
    private static long omniTimer = 0;

    public static void init() throws IOException
    {
        omnipotence = Loader.isModLoaded("omnipotence");

        File current = null;

        File f = new File(MCTools.getConfigDir() + ".." + File.separator + "logs" + File.separator + "currentgclogfilename.txt");
        if (f.exists())
        {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            current = new File(MCTools.getConfigDir() + ".." + File.separator + "logs" + File.separator + reader.readLine() + "." + EXT);
            reader.close();
        }


        //Remove old, already processed files
        f = new File(MCTools.getConfigDir() + ".." + File.separator + "logs");
        if (f.exists())
        {
            File[] files = f.listFiles();
            if (files != null)
            {
                for (File file : files)
                {
                    String fullname = file.getName();
                    int index = fullname.lastIndexOf(".");
                    String ext = index == -1 ? "" : fullname.substring(index + 1);

                    if (ext.equals(EXT) && (current == null || !file.getAbsolutePath().equals(current.getAbsolutePath()))) file.delete();
                }
            }
        }


        //Begin processing new file
        if (current != null) init(current);
    }

    private static void init(File file) throws IOException
    {

        reader = new BufferedReader(new FileReader(file));

        MinecraftForge.EVENT_BUS.register(ServerTickTimer.class);
        MinecraftForge.EVENT_BUS.register(GCMessageFixer.class);
    }

    @SubscribeEvent
    public static void update(TickEvent.ServerTickEvent event) throws IOException
    {
        if (gcMessageMode >= 1 && omnipotence)
        {
            if (++omniTimer == 20) //1 second after last processLine() call
            {
                System.out.println(Debug.memData());
                System.out.println();
            }
        }

        if (ServerTickTimer.currentTick() % DELAY == 0)
        {
            int i = reader.read();
            while (i != -1)
            {
                char c = (char) i;
                switch (c)
                {
                    case '\r':
                        break;
                    case '\n':
                        processLine();
                        break;
                    default:
                        current.append(c);
                }

                i = reader.read();
            }
        }
    }

    private static void processLine()
    {
        if (gcMessageMode == 2) System.out.println(current);
        current = new StringBuilder();
        if (omnipotence) omniTimer = 0;
    }
}
