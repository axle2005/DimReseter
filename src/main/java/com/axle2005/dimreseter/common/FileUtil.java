package com.axle2005.dimreseter.common;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;

import com.axle2005.dimreseter.sponge.DimReseter;
import com.axle2005.dimreseter.sponge.Region;

import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimManager;

public class FileUtil {

	
	
	public static File[] getRegions(File worldFolder) {
		
		
		File[] files = null;
		if (worldFolder.isDirectory()) {
			files = worldFolder.listFiles();
		}
		return files;
	}
	public static String[] getRegions(String path){
		String[] entries = null;
		String newPath = path + File.separator +"region";
		File location = new File(newPath);
		if (location.isDirectory()) {
			entries = location.list();
		}
		return entries;
		
	}
	
	public static void expireRegions(String worldName, int expirationDays) {
		List<File> regions = new ArrayList<File>();
		List<String> claimRegions = new ArrayList<String>();
		
		final Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -expirationDays);
		final long oldestTime = c.getTimeInMillis();
		
		
		
		
		World world = Sponge.getServer().getWorld(worldName).get();
		//String directory = world.getDirectory().toString();
		//String newPath = directory+ File.separator + "region";
		
		if (DimReseter.getInstance().isGPPresent()) {
			
			ClaimManager cm = DimReseter.getGPApi().getClaimManager(world);
			for (Claim cl : cm.getWorldClaims()) {
				for(Chunk ch : cl.getChunks()) {
					if(!claimRegions.contains(Region.getRegionFromChunk(ch.getPosition()))) {
						claimRegions.add(Region.getRegionFromChunk(ch.getPosition()));
						DimReseter.getInstance().getLogger().info(Region.getRegionFromChunk(ch.getPosition()));
					}
					
				}
			}

		}
		
		
		
		//File location = new File(newPath);
		File location = world.getDirectory().toFile();
		if (location.isDirectory()) {
			for (String f : location.list()) {
				
				File currentFile = new File(location.getPath(), f);
				if (currentFile.lastModified() < oldestTime) {
					if(DimReseter.getInstance().isGPPresent()) {
						if(!claimRegions.contains(currentFile.getName())) {
							regions.add(currentFile);
						}
					}
					else {
						regions.add(currentFile);	
					}
					
				}
			}
		}
		for(File f : regions) {
			DimReseter.getInstance().getLogger().info("Regions: "+f.getName());
		}
	
		
		
	}
		
	public static void clearAllRegions(String path) {
		String newPath = path + File.separator +"region";
		File location = new File(newPath);
		if (location.isDirectory()) {
			String[] entries = location.list();
			for (String s : entries) {
				File currentFile = new File(location.getPath(), s);
				currentFile.delete();
			}

		}
	}
	
	public static void clearData(String path) {
		String newPath = path + File.separator +"data";
		File location = new File(newPath);
		if (location.isDirectory()) {
			String[] entries = location.list();
			for (String s : entries) {
				File currentFile = new File(location.getPath(), s);
				if (currentFile.getName().equals("villages_end.dat")
						|| currentFile.getName().equals("villages_nether.dat")
						|| currentFile.getName().equals("EndCity.dat")) {
					currentFile.delete();
				}

			}

		}
	}
		
	
}
