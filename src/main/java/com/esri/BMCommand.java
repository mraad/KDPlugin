package com.esri;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 */
public final class BMCommand implements CommandExecutor
{
    private final KDPlugin m_plugin;

    public BMCommand(
            final KDPlugin plugin)
    {
        m_plugin = plugin;
    }

    @Override
    public boolean onCommand(
            final CommandSender commandSender,
            final Command command,
            final String label,
            final String[] args)
    {
        boolean rc = false;
        if (args.length > 1)
        {
            final double maxHeight = args.length > 2 ? Double.parseDouble(args[3]) : 30.0;
            final double maxValue = args.length > 2 ? Double.parseDouble(args[2]) : 20000.0;
            final byte color = args.length > 1 ? Byte.parseByte(args[1]) : 1;
            m_plugin.populateWorld(args[0], color, maxValue, maxHeight);
            rc = true;
        }
        return rc;
    }

}
