package com.axle2005.dimreseter.sponge;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimManager;

public class Reset {

	DimReseter plugin;
	Boolean gpPresent;
	Map<UUID, Claim> claimData = new ConcurrentHashMap<>();
	
	public Reset(DimReseter plugin) {

		this.plugin = plugin;
	}
	
	public void deleteRegions(String dim) {
		String path = Sponge.getGame().getGameDirectory() + File.separator + "world" + File.separator + dim;
		String path2 = path + File.separator + "data";
				
		File location2 = new File(path2);
		if (location2.isDirectory()) {
			String[] entries = location2.list();
			for (String s : entries) {
				File currentFile = new File(location2.getPath(), s);
				if (currentFile.getName().equals("villages_end.dat")
						|| currentFile.getName().equals("villages_nether.dat")
						|| currentFile.getName().equals("EndCity.dat")) {
					currentFile.delete();
				}

			}

		}
		String path3 = path + File.separator + "region";
		resetUtil(path3);

	}
	public void clearClaims(String dim, Boolean gpPresent)
	{
		if(gpPresent)
		{
			
			Optional<World> world = Sponge.getServer().getWorld(dim);
			if(world.isPresent())
			{
				World w = world.get();
				ClaimManager cm = plugin.getGPApi().getClaimManager(w);	
				List<Claim> claims = cm.getWorldClaims();
				for(Claim c : claims)
				{
					
					if(c.isBasicClaim())
					{
						
						claimData.put(c.getUniqueId(), c);
					}
					
				}
				for(Claim c : claimData.values()){
					cm.deleteClaim(c, true);
					
					
				}
			}
			plugin.getLogger().info("Deleted Claims in Dim: "+dim);
		}
	}

	private void resetUtil(String path) {
		File location = new File(path);

		if (location.isDirectory()) {
			String[] entries = location.list();
			for (String s : entries) {
				File currentFile = new File(location.getPath(), s);

				currentFile.delete();

			}

		}
	}

}