package com.axle2005.dimreseter.sponge;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Chunk;

import com.flowpowered.math.vector.Vector3i;

public class Region {

	public Region(){
		
	}
	public static Chunk[] getRegionChunks(int x,int z, String worldname)
	{
		Chunk[] chunks = new Chunk[1024];
		int length =15;
		int width = 15;
		int count =0;
		while(length <= 512)
		{
			width = 15;
			while(width <=512)
			{
				chunks[count] = Sponge.getServer().getWorld(worldname).get().getChunkAtBlock(length, 0, width).get();
				count++;
				width=width+16;
			}
			length= length +16;
		}
		
		
		return chunks;
		
	}
	public static String getRegionFromChunk(Vector3i chunk) {
		return "r."+chunk.getX()+"."+chunk.getZ()+".mca";
	}
	public int getX(Player player){
		int x = player.getLocation().getChunkPosition().getX() / 32;
		return x;
	}
	public int getZ(Player player)
	{
		int z = player.getLocation().getChunkPosition().getZ() / 32;
		return z;
	}

	public Vector3i getCenter(int x, int z)
	{
		int xcheck = 1;
		int zcheck = 1;
		if(x<0){
			xcheck=-1;
			x=x*xcheck;
		}
		if(z<0)
		{
			zcheck=-1;
			z=z*zcheck;
		}
		int xcenter = ((x*512)+256)*xcheck;
		int zcenter = ((z*512)+256)*zcheck;
		
		Vector3i block = new Vector3i(xcenter,100,zcenter);
		//Vector3i center = Sponge.getServer().getWorld("world").get().getBlock
		return block;
		
	}
}
