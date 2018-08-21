package com.axle2005.dimreseter.sponge;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import com.flowpowered.math.vector.Vector3i;

public class Util {

	public static void spawnPlatform(String dimname) {
		if(Sponge.getServer().getWorld(dimname).isPresent())
		{
			World world = Sponge.getServer().getWorld(dimname).get();
			WorldProperties prop = world.getWorldStorage().getWorldProperties();
			int x = prop.getSpawnPosition().getX();
			int y = prop.getSpawnPosition().getY();
			int z = prop.getSpawnPosition().getZ();

			Vector3i[] plat = new Vector3i[9];
			for (int i = 0; i <= 9; i++) {

				plat[0] = new Vector3i(x, y - 2, z);
				plat[1] = new Vector3i(x, y - 2, z + 1);
				plat[7] = new Vector3i(x, y - 2, z - 1);
				plat[3] = new Vector3i(x - 1, y - 2, z + 1);
				plat[4] = new Vector3i(x - 1, y - 2, z);
				plat[6] = new Vector3i(x - 1, y - 2, z - 1);
				plat[5] = new Vector3i(x + 1, y - 2, z);
				plat[2] = new Vector3i(x + 1, y - 2, z + 1);
				plat[8] = new Vector3i(x + 1, y - 2, z - 1);

			}

			for (Vector3i v : plat) {

				world.getLocation(v).setBlockType(BlockTypes.BEDROCK);

			}
		}


	}
    
}
