package com.esri;

/**
 */
public final class BlockCoord
{
    public int x;
    public int z;

    public BlockCoord()
    {
    }

    public BlockCoord(
            final int x,
            final int z)
    {
        this.x = x;
        this.z = z;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof BlockCoord))
        {
            return false;
        }

        final BlockCoord blockCoord = (BlockCoord) o;

        if (x != blockCoord.x)
        {
            return false;
        }
        if (z != blockCoord.z)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = x;
        result = 31 * result + z;
        return result;
    }
}
