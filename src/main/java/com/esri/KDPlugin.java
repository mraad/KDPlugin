package com.esri;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.terracotta.toolkit.Toolkit;
import org.terracotta.toolkit.ToolkitFactory;
import org.terracotta.toolkit.ToolkitInstantiationException;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 */
public class KDPlugin extends JavaPlugin
{
    // http://www.minecraftwiki.net/wiki/Data_values#Wool
    private static final int AIR = Material.AIR.getId();
    private static final int WOOL = Material.WOOL.getId();

    private final Map<String, Set<BlockCoord>> m_map = new HashMap<String, Set<BlockCoord>>();
    private final Map<String, RasterInfo> m_rasterInfoMap = new HashMap<String, RasterInfo>();
    private Toolkit m_toolkit;

    public KDPlugin()
    {
    }

    @Override
    public void onEnable()
    {
        getCommand("bm").setExecutor(new BMCommand(this));
        try
        {
            m_toolkit = ToolkitFactory.createToolkit(KDConst.TOOLKIT_URI_VAL);
        }
        catch (ToolkitInstantiationException e)
        {
            getLogger().log(Level.SEVERE, e.toString(), e);
        }
    }

    @Override
    public void onDisable()
    {
        if (m_toolkit != null)
        {
            m_toolkit.shutdown();
        }
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(
            final String worldName,
            final String args)
    {
        ChunkGenerator chunkGenerator = null;
        if (args.endsWith(".flt"))
        {
            final RasterInfo rasterInfo = new RasterInfo();
            try
            {
                rasterInfo.loadRaster(args);
                chunkGenerator = new RasterGenerator(rasterInfo);
                m_rasterInfoMap.put(worldName, rasterInfo);
            }
            catch (IOException e)
            {
                getLogger().warning(e.toString());
            }
        }
        return chunkGenerator;
    }

    private RasterInfo getRasterInfo(final String worldName)
    {
        return m_rasterInfoMap.get(worldName);
    }

    private Set<KDItem> getSet(final String worldName)
    {
        return m_toolkit.getSet(worldName + "Set", KDItem.class);
    }

    private Map<String, Double> getMap(final String worldName)
    {
        return m_toolkit.getMap(worldName + "Map", String.class, Double.class);
    }

    public void populateWorld(
            final String worldName,
            final byte color,
            final double maxValue,
            final double maxHeight
    )
    {
        final World world = getServer().getWorld(worldName);
        if (world != null)
        {
            final RasterInfo rasterInfo = getRasterInfo(worldName);

            final Set<BlockCoord> set = emptyLastSet(worldName, world);

            final Map<String, Double> map = getMap(worldName);
            final double xmin = map.get(KDConst.XMIN_KEY);
            final double ymin = map.get(KDConst.YMIN_KEY);
            final double cell = map.get(KDConst.CELL_KEY);
            final double ofs = cell * 0.5;
            for (final KDItem item : getSet(worldName))
            {
                final double lon = GeoHash.toX(item.geohash, xmin, cell);
                final double lat = GeoHash.toY(item.geohash, ymin, cell);
                final int x = rasterInfo.toGridX(lon + ofs);
                final int z = rasterInfo.toGridY(lat + ofs);
                final double val = Math.min(maxValue, item.value);
                final int ymax = (int) (1.0 + Math.ceil(maxHeight * val / maxValue));
                set.add(new BlockCoord(x, z));
                int y = 0;
                while (y <= ymax)
                {
                    world.getBlockAt(x, ++y, z).setTypeIdAndData(WOOL, color, false);
                }
            }
        }
        else
        {
            getLogger().warning("Cannot find world " + worldName);
        }
    }

    private Set<BlockCoord> emptyLastSet(
            final String worldName,
            final World world)
    {
        Set<BlockCoord> set = m_map.get(worldName);
        if (set != null)
        {
            for (final BlockCoord blockCoord : set)
            {
                int y = 0, ymax = world.getHighestBlockYAt(blockCoord.x, blockCoord.z);
                while (y <= ymax)
                {
                    world.getBlockAt(blockCoord.x, ++y, blockCoord.z).setTypeIdAndData(AIR, (byte) 0, false);
                }
            }
            set.clear();
        }
        else
        {
            set = new HashSet<BlockCoord>();
            m_map.put(worldName, set);
        }
        return set;
    }

}
