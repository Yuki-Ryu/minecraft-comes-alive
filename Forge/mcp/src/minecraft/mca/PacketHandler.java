/*******************************************************************************
 * PacketHandler.java
 * Copyright (c) 2013 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package mca;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.NetworkListenThread;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Handles packets received both client and server side.
 */
public class PacketHandler implements IPacketHandler
{
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player senderPlayer) 
	{
		try
		{
			MCA.instance.logDebug("Received packet: " + packet.channel + ". Size = " + packet.length);

			if (packet.channel.equals("MCA_F_REQ"))
			{
				handleFieldRequest(packet, senderPlayer);
			}

			else if (packet.channel.equals("MCA_F_VAL"))
			{
				handleFieldValue(packet, senderPlayer);
			}

			else if (packet.channel.equals("MCA_TARGET"))
			{
				handleTarget(packet, senderPlayer);
			}

			else if (packet.channel.equals("MCA_REMOVEITEM"))
			{
				handleRemoveItem(packet, senderPlayer);
			}

			else if (packet.channel.equals("MCA_ACHIEV"))
			{
				handleAchievement(packet, senderPlayer);
			}

			else if (packet.channel.equals("MCA_SYNC"))
			{
				handleSync(packet, senderPlayer);
			}

			else if (packet.channel.equals("MCA_SYNC_REQ"))
			{
				handleSyncRequest(packet, senderPlayer);
			}

			else if (packet.channel.equals("MCA_ENGAGE"))
			{
				handleEngagement(packet, senderPlayer);
			}

			else if (packet.channel.equals("MCA_ADDITEM"))
			{
				handleAddItem(packet, senderPlayer);
			}

			else if (packet.channel.equals("MCA_FAMTREE"))
			{
				handleFamilyTree(packet, senderPlayer);
			}

			else if (packet.channel.equals("MCA_DROPITEM"))
			{
				handleDropItem(packet, senderPlayer);
			}

			else if (packet.channel.equals("MCA_INVENTORY"))
			{
				handleInventory(packet, senderPlayer);
			}

			else if (packet.channel.equals("MCA_CHORE"))
			{
				handleChore(packet, senderPlayer);
			}

			else if (packet.channel.equals("MCA_TOMB"))
			{
				handleTombstone(packet, senderPlayer);
			}

			else if (packet.channel.equals("MCA_TOMB_REQ"))
			{
				handleTombstoneRequest(packet, senderPlayer);
			}

			else if (packet.channel.equals("MCA_POSITION"))
			{
				handlePosition(packet, senderPlayer);
			}

			else if (packet.channel.equals("MCA_REMOVEITEM"))
			{
				handleRemoveItem(packet, senderPlayer);
			}

			else if (packet.channel.equals("MCA_KILL"))
			{
				handleKill(packet, senderPlayer);
			}

			else if (packet.channel.equals("MCA_LOGIN"))
			{
				handleLogin(packet, senderPlayer);
			}

			else if (packet.channel.equals("MCA_WORLDPROP"))
			{
				handleWorldProperties(packet, senderPlayer);
			}

			else if (packet.channel.equals("MCA_SAYLOCAL"))
			{
				handleSayLocalized(packet, senderPlayer);
			}

			else if (packet.channel.equals("MCA_PLMARRY"))
			{
				handlePlayerMarried(packet, senderPlayer);
			}

			else if (packet.channel.equals("MCA_HAVEBABY"))
			{
				handleHaveBaby(packet, senderPlayer);
			}

			else if (packet.channel.equals("MCA_BABYINFO"))
			{
				handleBabyInfo(packet, senderPlayer);
			}

			else if (packet.channel.equals("MCA_TRADE"))
			{
				handleTrade(packet, senderPlayer);
			}

			else if (packet.channel.equals("MCA_RESPAWN"))
			{
				handleRespawn(packet, senderPlayer);
			}

			else if (packet.channel.equals("MCA_VPPROC"))
			{
				handleVillagerPlayerProcreate(packet, senderPlayer);
			}
		}

		catch (Throwable e)
		{
			MCA.instance.log(e);
		}
	}

	/**
	 * Handles a packet that requests the value of a field. This kind of packet is only sent by the client.
	 * 
	 * @param 	packet	The packet containing the field request data.
	 * @param 	player	The player that the packet came from.
	 */
	private void handleFieldRequest(Packet250CustomPayload packet, Player senderPlayer) throws IOException, ClassNotFoundException, IllegalAccessException, SecurityException, NoSuchFieldException
	{
		byte[] data = Utility.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		World world = ((EntityPlayer)senderPlayer).worldObj;

		//Assign received data.
		int entityId     = (Integer)objectInput.readObject();
		String fieldName = (String)objectInput.readObject();

		//Loop through server entities and send the field's value to the player.
		for (Object obj : world.loadedEntityList)
		{
			Entity entity = (Entity)obj;

			if (entity.entityId == entityId)
			{
				//Workaround for protected field "texture".
				if (fieldName.equals("texture"))
				{
					PacketDispatcher.sendPacketToPlayer(PacketCreator.createFieldValuePacket(entityId, fieldName, ((EntityBase)entity).getTexture()), senderPlayer);
					break;
				}

				else
				{
					Field field = entity.getClass().getField(fieldName);
					PacketDispatcher.sendPacketToPlayer(PacketCreator.createFieldValuePacket(entityId, fieldName, field.get(entity)), senderPlayer);
					break;
				}
			}
		}
	}

	/**
	 * Handles a packet that contains the value that a field should be changed to.
	 * 
	 * @param 	packet	The packet containing the field value data.
	 * @param 	player	The player that the packet came from.
	 */
	private void handleFieldValue(Packet250CustomPayload packet, Player senderPlayer) throws IOException, ClassNotFoundException, IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException
	{
		byte[] data = Utility.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		EntityPlayer player = (EntityPlayer)senderPlayer;
		World world = player.worldObj;

		//Assign received data.
		int entityId     = (Integer)objectInput.readObject();
		String fieldName = (String)objectInput.readObject();
		Object fieldValue = objectInput.readObject();

		for (Object obj : world.loadedEntityList)
		{
			Entity entity = (Entity)obj;

			if (entity.entityId == entityId)
			{
				for (Field f : entity.getClass().getFields())
				{
					if (!fieldName.equals("texture"))
					{
						if (f.getName().equals(fieldName))
						{
							if (f.getType().getName().contains("boolean"))
							{
								entity.getClass().getField(fieldName).set(entity, Boolean.parseBoolean(fieldValue.toString()));

								//Special condition. When isSpouse is changed, a villager's AI must be updated just in case it is a guard who is
								//either getting married or getting divorced.
								if (f.getName().equals("isSpouse"))
								{
									EntityBase entityBase = (EntityBase)entity;
									entityBase.addAI();
								}
							}

							else if (f.getType().getName().contains("int"))
							{
								entity.getClass().getField(fieldName).set(entity, Integer.parseInt(fieldValue.toString()));

								if (f.getName().equals("traitId"))
								{
									EntityBase entityBase = (EntityBase)entity;
									entityBase.trait = EnumTrait.getTraitById(entityBase.traitId);
								}
							}

							else if (f.getType().getName().contains("double"))
							{
								entity.getClass().getField(fieldName).set(entity, Double.parseDouble(fieldValue.toString()));
							}

							else if (f.getType().getName().contains("float"))
							{
								entity.getClass().getField(fieldName).set(entity, Float.parseFloat(fieldValue.toString()));

								EntityBase entityBase = (EntityBase)entity;
								entityBase.setMoodByMoodPoints(false);
							}

							else if (f.getType().getName().contains("String"))
							{
								entity.getClass().getField(fieldName).set(entity, fieldValue.toString());
							}

							else if (f.getType().getName().contains("Map"))
							{
								if (f.getName().equals("playerMemoryMap"))
								{
									//Player name must be set if the map is a memory map since it is transient.
									Map<String, PlayerMemory> memoryMap = (Map<String, PlayerMemory>)fieldValue;
									PlayerMemory memory = memoryMap.get(player.username);

									if (memory != null)
									{
										memory.playerName = player.username;
										memoryMap.put(player.username, memory);
									}
									
									else
									{
										memoryMap.put(player.username, new PlayerMemory(player.username));
									}
									
									entity.getClass().getField(fieldName).set(entity, memoryMap);
								}

								else
								{
									entity.getClass().getField(fieldName).set(entity, fieldValue);
								}
							}
						}
					}

					else
					{
						((EntityBase)entity).setTexture(fieldValue.toString());
					}
				}

				break;
			}
		}

		//Sync with all other players if server side.
		if (!world.isRemote)
		{
			PacketDispatcher.sendPacketToAllPlayers(PacketCreator.createSyncPacket((EntityBase) world.getEntityByID(entityId)));
		}
	}

	/**
	 * Handles a packet meant to assign a target to an entity.
	 * 
	 * @param 	packet	The packet containing the target data.
	 * @param 	player	The player that the packet came from.
	 */
	private void handleTarget(Packet250CustomPayload packet, Player senderPlayer) throws ClassNotFoundException, IOException
	{
		byte[] data = Utility.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		World world = ((EntityPlayer)senderPlayer).worldObj;

		//Assign received data.
		int entityId = (Integer)objectInput.readObject();
		int targetId = (Integer)objectInput.readObject();

		for (Object obj : world.loadedEntityList)
		{
			Entity entity = (Entity)obj;

			if (entity.entityId == entityId)
			{
				EntityBase genderizedEntity = (EntityBase)entity;

				if (targetId == 0)
				{
					genderizedEntity.target = null;
				}

				else
				{
					genderizedEntity.target = (EntityLivingBase)genderizedEntity.worldObj.getEntityByID(targetId);
				}
			}
		}
	}

	/**
	 * Handles a packet that removes an item from the player's inventory.
	 * 
	 * @param 	packet	The packet containing the item and player data.
	 * @param 	player	The player that the packet came from.
	 */
	private void handleRemoveItem(Packet250CustomPayload packet, Player senderPlayer) throws ClassNotFoundException, IOException
	{
		byte[] data = Utility.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		//Assign received data.
		int playerID = (Integer)objectInput.readObject();
		int slotID = (Integer)objectInput.readObject();
		int quantity = (Integer)objectInput.readObject();
		int damage = (Integer)objectInput.readObject();

		EntityPlayer player = (EntityPlayer)senderPlayer;
		ItemStack itemStack = player.inventory.mainInventory[slotID];

		//Consume one of that item in the player's inventory.
		int nextStackSize = itemStack.stackSize - quantity;

		//Check if the next size is zero or below, meaning it must be null.
		if (nextStackSize <= 0)
		{
			player.inventory.setInventorySlotContents(slotID, null);
		}

		//The new stack size is greater than zero.
		else
		{
			ItemStack newItemStack = new ItemStack(itemStack.getItem(), nextStackSize, damage);
			player.inventory.setInventorySlotContents(slotID, newItemStack);
		}
	}

	/**
	 * Handles a packet that unlocks an achievement for a player.
	 * 
	 * @param 	packet	The packet containing the achievement and player data.
	 * @param 	player	The player that the packet came from.
	 */
	private void handleAchievement(Packet250CustomPayload packet, Player senderPlayer) throws ClassNotFoundException, IOException
	{
		byte[] data = Utility.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		//Assign received data.
		int achievementID = (Integer)objectInput.readObject();
		int playerID = (Integer)objectInput.readObject();

		EntityPlayer player = (EntityPlayer)senderPlayer;

		for (Object obj : AchievementList.achievementList)
		{
			Achievement achievement = (Achievement)obj;

			if (achievement.statId == achievementID)
			{
				player.triggerAchievement(achievement);
				break;
			}
		}
	}

	/**
	 * Handles a packet that requests synchronization of a client and server side entity.
	 * 
	 * @param 	packet	The packet containing the sync request data.
	 * @param 	player	The player that send the sync request.
	 */
	private void handleSyncRequest(Packet250CustomPayload packet, Player senderPlayer) throws IllegalArgumentException, IllegalAccessException, IOException, ClassNotFoundException
	{
		byte[] data = Utility.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		World world = ((EntityPlayer)senderPlayer).worldObj;

		//Assign received data.
		int entityId = Integer.valueOf(objectInput.readObject().toString());
		objectInput.close();

		for (Object obj : world.loadedEntityList)
		{
			if (obj instanceof EntityBase)
			{
				EntityBase entity = (EntityBase)obj;

				if (entity.entityId == entityId)
				{
					PacketDispatcher.sendPacketToPlayer(PacketCreator.createSyncPacket(entity), senderPlayer);
					break;
				}
			}
		}
	}

	/**
	 * Handles a packet that contains synchronization data.
	 * 
	 * @param 	packet	The packet containing the synchrnoization data.
	 * @param 	player	The player that the packet came from.
	 */
	@SideOnly(Side.CLIENT)
	private void handleSync(Packet250CustomPayload packet, Player senderPlayer) throws IllegalArgumentException, IllegalAccessException, IOException, ClassNotFoundException
	{
		byte[] data = Utility.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		WorldClient world = Minecraft.getMinecraft().theWorld;

		//Assign received data.
		EntityBase receivedEntity = (EntityBase)objectInput.readObject();
		int receivedId = (Integer)objectInput.readObject();
		String receivedTexture = objectInput.readObject().toString();
		objectInput.close();

		EntityBase clientEntity = null;

		//Get the client side entity.
		clientEntity = (EntityBase)world.getEntityByID(receivedId);

		if (clientEntity.entityId == receivedId)
		{
			//Figure out which classes the entity is composed of.
			List<Class> classList = new ArrayList<Class>();
			classList.add(EntityBase.class);
			classList.add(receivedEntity.getClass());

			if (receivedEntity instanceof EntityPlayerChild || receivedEntity instanceof EntityVillagerChild)
			{
				classList.add(EntityChild.class);
			}

			//Loop through each field in each class that the received entity is made of and set the value of
			//the field on the client side entity to the same value as the received entity.
			for (Class c : classList)
			{
				for (Field f : c.getDeclaredFields())
				{
					try
					{
						//Workaround for chores not being assigned an owner.
						if (f.get(receivedEntity) instanceof Chore)
						{
							Chore theChore = (Chore)f.get(receivedEntity);
							theChore.owner = clientEntity;

							f.set(clientEntity, theChore);
						}

						//Workaround for family tree not being assigned an owner.
						else if (f.get(receivedEntity) instanceof FamilyTree)
						{
							FamilyTree theFamilyTree = (FamilyTree)f.get(receivedEntity);
							theFamilyTree.owner = clientEntity;

							f.set(clientEntity, theFamilyTree);
						}

						//Workaround for inventory not being assigned an owner.
						else if (f.get(receivedEntity) instanceof Inventory)
						{
							Inventory theInventory = (Inventory)f.get(receivedEntity);
							theInventory.owner = clientEntity;

							if (!clientEntity.inventory.equals(theInventory))
							{
								f.set(clientEntity, theInventory);
							}
						}

						else if (!Modifier.isFinal(f.getModifiers()) && !Modifier.isTransient(f.getModifiers()))
						{
							f.set(clientEntity, f.get(receivedEntity));
						}
					}

					catch (IllegalAccessException e)
					{
						continue;
					}
				}
			}

			//Tell the client entity that it no longer needs to sync and give it the received entity's texture.
			clientEntity.setTexture(receivedTexture);

			//Put the client entity's ID in the ids map.
			MCA.instance.idsMap.put(clientEntity.mcaID, clientEntity.entityId);

			//Set the entity mood and trait.
			clientEntity.setMoodByMoodPoints(false);
			clientEntity.trait = EnumTrait.getTraitById(clientEntity.traitId);

			return;
		}
	}

	/**
	 * Handles a packet that makes villagers have gifts for player who are engaged.
	 * 
	 * @param 	packet	The packet containing the engagement data.
	 * @param 	player	The player that the packet came from.
	 */
	private void handleEngagement(Packet250CustomPayload packet, Player senderPlayer) throws ClassNotFoundException, IOException 
	{
		byte[] data = Utility.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		EntityPlayer player = (EntityPlayer)senderPlayer;
		World world = player.worldObj;

		//Assign received data.
		int entityId = (Integer)objectInput.readObject();

		List<Entity> entitiesAroundMe = Logic.getAllEntitiesWithinDistanceOfEntity(world.getEntityByID(entityId), 64);

		for (Entity entity : entitiesAroundMe)
		{
			if (entity instanceof EntityVillagerAdult)
			{
				EntityVillagerAdult entityVillager = (EntityVillagerAdult)entity;

				if (entityVillager.playerMemoryMap.containsKey(player.username))
				{
					PlayerMemory memory = entityVillager.playerMemoryMap.get(player.username);
					memory.hasGift = true;
					entityVillager.playerMemoryMap.put(player.username, memory);
				}
			}
		}
	}

	/**
	 * Handles a packet that adds an item to the player's inventory.
	 * 
	 * @param 	packet	The packet containing the item data.
	 * @param 	player	The player that the packet came from.
	 */
	private void handleAddItem(Packet250CustomPayload packet, Player senderPlayer) throws ClassNotFoundException, IOException 
	{
		byte[] data = Utility.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		//Assign received data.
		int itemId = (Integer)objectInput.readObject();
		int playerId = (Integer)objectInput.readObject();

		EntityPlayer player = (EntityPlayer)senderPlayer;
		player.inventory.addItemStackToInventory(new ItemStack(itemId, 1, 0));
	}

	/**
	 * Handles a packet that updates a family tree.
	 * 
	 * @param	packet	The packet containing the family tree data.
	 * @param player 
	 */
	private void handleFamilyTree(Packet250CustomPayload packet, Player senderPlayer) throws IOException, ClassNotFoundException
	{
		byte[] data = Utility.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		World world = ((EntityPlayer)senderPlayer).worldObj;

		//Assign received data.
		int entityId = Integer.valueOf(objectInput.readObject().toString());
		FamilyTree familyTree = (FamilyTree)objectInput.readObject();

		objectInput.close();

		EntityBase entity = (EntityBase)world.getEntityByID(entityId);
		familyTree.owner = entity;
		entity.familyTree = familyTree;
	}

	/**
	 * Handles a packet that makes an entity drop an item that is not in its inventory.
	 * 
	 * @param 	packet	The packet containing the drop item data.
	 * @param 	player	The player that the packet came from.
	 */
	private void handleDropItem(Packet250CustomPayload packet, Player senderPlayer) throws ClassNotFoundException, IOException 
	{
		byte[] data = Utility.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		World world = ((EntityPlayer)senderPlayer).worldObj;

		//Assign received data.
		int entityId = (Integer)objectInput.readObject();
		int itemId = (Integer)objectInput.readObject();
		int count = (Integer)objectInput.readObject();

		EntityBase entity = (EntityBase)world.getEntityByID(entityId);
		entity.dropItem(itemId, count);
	}

	/**
	 * Handles a packet that updates an inventory.
	 * 
	 * @param	packet	The packet containing the inventory data.
	 * @param 	player	The player that the packet came from.
	 */
	private void handleInventory(Packet250CustomPayload packet, Player senderPlayer) throws NumberFormatException, IOException, ClassNotFoundException
	{
		byte[] data = Utility.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		World world = ((EntityPlayer)senderPlayer).worldObj;

		//Assign received data.
		int entityId = Integer.valueOf(objectInput.readObject().toString());
		Inventory inventory = (Inventory)objectInput.readObject();
		objectInput.close();

		EntityBase entity = (EntityBase)world.getEntityByID(entityId);
		inventory.owner = entity;
		entity.inventory = inventory;
	}

	/**
	 * Handles a packet that updates a chore.
	 * 
	 * @param	packet	The packet containing the chore data.
	 * @param 	player	The player that the packet came from.
	 */
	private void handleChore(Packet250CustomPayload packet, Player senderPlayer) throws ClassNotFoundException, IOException
	{
		byte[] data = Utility.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		World world = ((EntityPlayer)senderPlayer).worldObj;

		//Assign received data.
		int entityId = (Integer)objectInput.readObject();
		Chore chore = (Chore)objectInput.readObject();
		objectInput.close();

		EntityBase entity = (EntityBase)world.getEntityByID(entityId);
		chore.owner = entity;

		if (chore instanceof ChoreFarming)
		{
			entity.farmingChore = (ChoreFarming) chore;
		}

		else if (chore instanceof ChoreWoodcutting)
		{
			entity.woodcuttingChore = (ChoreWoodcutting) chore;
		}

		else if (chore instanceof ChoreFishing)
		{
			entity.fishingChore = (ChoreFishing) chore;
		}

		else if (chore instanceof ChoreMining)
		{
			entity.miningChore = (ChoreMining) chore;
		}

		else if (chore instanceof ChoreCombat)
		{
			entity.combatChore = (ChoreCombat) chore;
		}

		else if (chore instanceof ChoreHunting)
		{
			entity.huntingChore = (ChoreHunting) chore;
		}

		else
		{
			MCA.instance.log("Unidentified chore type received when handling chore packet.");
		}
	}

	/**
	 * Handles a packet that updates the text on a tombstone.
	 * 
	 * @param	packet	The packet that contains the tombstone data.
	 * @param 	player	The player that the packet came from.
	 */
	private void handleTombstone(Packet250CustomPayload packet, Player senderPlayer) throws ClassNotFoundException, IOException
	{
		byte[] data = Utility.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		World world = ((EntityPlayer)senderPlayer).worldObj;

		//Assign received data.
		int xCoord = (Integer) objectInput.readObject();
		int yCoord = (Integer) objectInput.readObject();
		int zCoord = (Integer) objectInput.readObject();
		String line1 = (String) objectInput.readObject();
		String line2 = (String) objectInput.readObject();
		String line3 = (String) objectInput.readObject();
		String line4 = (String) objectInput.readObject();

		objectInput.close();

		TileEntityTombstone tombstone = (TileEntityTombstone)world.getBlockTileEntity(xCoord, yCoord, zCoord);
		tombstone.signText[0] = line1;
		tombstone.signText[1] = line2;
		tombstone.signText[2] = line3;
		tombstone.signText[3] = line4;
	}

	/**
	 * Handles a packet that requests an update for a tombstone.
	 * 
	 * @param 	packet	The packet that contains the tombstone request data.
	 * @param 	player	The player that the packet came from.
	 */
	private void handleTombstoneRequest(Packet250CustomPayload packet, Player senderPlayer) throws ClassNotFoundException, IOException
	{
		byte[] data = Utility.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		World world = ((EntityPlayer)senderPlayer).worldObj;

		//Assign received data.
		int xCoord = (Integer)objectInput.readObject();
		int yCoord = (Integer)objectInput.readObject();
		int zCoord = (Integer)objectInput.readObject();

		TileEntityTombstone tombstone = (TileEntityTombstone)world.getBlockTileEntity(xCoord, yCoord, zCoord);
		PacketDispatcher.sendPacketToAllPlayers(PacketCreator.createTombstonePacket(tombstone));
	}

	/**
	 * Handles a packet that sets an entity's position.
	 * 
	 * @param 	packet	The packet that contains the position information.
	 * @param 	player	The player that the packet came from.
	 */
	private void handlePosition(Packet250CustomPayload packet, Player senderPlayer) throws ClassNotFoundException, IOException
	{
		byte[] data = Utility.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		World world = ((EntityPlayer)senderPlayer).worldObj;

		//Assign received data.
		int entityId = (Integer)objectInput.readObject();
		double xCoord = (Double)objectInput.readObject();
		double yCoord = (Double)objectInput.readObject();
		double zCoord = (Double)objectInput.readObject();

		Entity entity = world.getEntityByID(entityId);
		entity.setPosition(xCoord, yCoord, zCoord);
	}

	/**
	 * Handles a packet that kills an entity.
	 * 
	 * @param 	packet	The packet that contains the kill information.
	 * @param 	player	The player that the packet came from.
	 */
	private void handleKill(Packet250CustomPayload packet, Player senderPlayer) throws ClassNotFoundException, IOException
	{
		byte[] data = Utility.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		World world = ((EntityPlayer)senderPlayer).worldObj;

		//Assign received data.
		int entityId = (Integer)objectInput.readObject();

		EntityBase entity = (EntityBase)world.getEntityByID(entityId);
		entity.setDeadWithoutNotification();
	}

	/**
	 * Handles a login packet.
	 * 
	 * @param 	packet	The packet containing the login information.
	 * @param	player	The player that the packet came from.
	 */
	private void handleLogin(Packet250CustomPayload packet, Player senderPlayer) throws IOException, ClassNotFoundException
	{
		byte[] data = Utility.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		World world = ((EntityPlayer)senderPlayer).worldObj;
		EntityPlayer player = (EntityPlayer)senderPlayer;

		//Assign received data.
		ModPropertiesManager modPropertiesManager = (ModPropertiesManager) objectInput.readObject();

		//Ensure item IDs are the same.
		if (modPropertiesManager.equals(MCA.instance.modPropertiesManager))
		{
			//Give the player a world settings manager.
			WorldPropertiesManager manager = new WorldPropertiesManager(world.getSaveHandler().getWorldDirectoryName(), player.username);

			MCA.instance.playerWorldManagerMap.put(player.username, manager);

			//Send it to the client.
			PacketDispatcher.sendPacketToPlayer(PacketCreator.createWorldPropertiesPacket(manager), senderPlayer);
		}

		else
		{
			((EntityPlayerMP)senderPlayer).playerNetServerHandler.kickPlayerFromServer("Minecraft Comes Alive: Server item IDs do not match your own. You cannot log in.");
		}
	}

	/**
	 * Handles a world properties packet.
	 * 
	 * @param 	packet	The packet containing the world properties information.
	 * @param	player	The player that the packet came from.
	 */
	private void handleWorldProperties(Packet250CustomPayload packet, Player senderPlayer) throws IOException, ClassNotFoundException
	{
		byte[] data = Utility.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		World world = ((EntityPlayer)senderPlayer).worldObj;
		EntityPlayer player = (EntityPlayer)senderPlayer;

		//Assign received data.
		WorldPropertiesManager manager = (WorldPropertiesManager)objectInput.readObject();
		MCA.instance.logDebug("Received world properties manager for " + ((EntityPlayer)senderPlayer).username);

		//Client side.
		if (world.isRemote)
		{
			MCA.instance.playerWorldManagerMap.put(player.username, manager);
		}

		//Server side.
		else
		{
			//Update only the actual properties on the old manager to retain the ability to save.
			WorldPropertiesManager oldWorldPropertiesManager = MCA.instance.playerWorldManagerMap.get(player.username);
			oldWorldPropertiesManager.worldProperties = manager.worldProperties;

			//Put the changed manager back into the map and save it.
			MCA.instance.playerWorldManagerMap.put(player.username, oldWorldPropertiesManager);
			oldWorldPropertiesManager.saveWorldProperties();
		}
	}

	/**
	 * Handles a say localized packet.
	 * 
	 * @param 	packet	The packet containing the say localized information.
	 * @param	player	The player that the packet came from.
	 */
	private void handleSayLocalized(Packet250CustomPayload packet, Player senderPlayer) throws ClassNotFoundException, IOException
	{
		byte[] data = Utility.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		EntityPlayer player = (EntityPlayer)senderPlayer;
		World worldObj = player.worldObj;

		EntityPlayer receivedPlayer = null;

		//Initialize fields to be filled.
		String playerName = null;
		Integer entityId = null;
		String phraseId = null;
		Boolean useCharacterType = null;
		String prefix = null;
		String suffix = null;

		//Assign received data.
		try
		{
			playerName = (String) objectInput.readObject();

			if (worldObj.getPlayerEntityByName(playerName) != null)
			{
				receivedPlayer = worldObj.getPlayerEntityByName(playerName);
			}
		}

		catch (NullPointerException e)
		{
			MCA.instance.log(e);
		}

		try
		{
			entityId = (Integer) objectInput.readObject();
		}

		catch (NullPointerException e)
		{
		}

		try
		{
			phraseId = (String) objectInput.readObject();
		}

		catch (NullPointerException e)
		{
		}

		try
		{
			useCharacterType = (Boolean) objectInput.readObject();
		}

		catch (NullPointerException e)
		{
		}

		try
		{
			prefix = (String) objectInput.readObject();
		}

		catch (NullPointerException e)
		{	
		}

		try
		{
			suffix = (String) objectInput.readObject();
		}

		catch (NullPointerException e)
		{
		}

		//Get the entity that should be speaking if there is one.
		if (entityId != null)
		{
			EntityBase speaker = (EntityBase)worldObj.getEntityByID(entityId);

			if (receivedPlayer != null)
			{
				speaker.say(Localization.getString(receivedPlayer, speaker, phraseId, useCharacterType, prefix, suffix));
			}

			else
			{
				speaker.say(Localization.getString(player, speaker, phraseId, useCharacterType, prefix, suffix));
			}
		}

		//There isn't a speaker, so just add the localized string to the player's chat log.
		else
		{
			if (receivedPlayer != null)
			{
				player.addChatMessage(Localization.getString(receivedPlayer, null, phraseId, useCharacterType, prefix, suffix));
			}

			else
			{
				player.addChatMessage(Localization.getString(player, null, phraseId, useCharacterType, prefix, suffix));
			}
		}
	}

	private void handlePlayerMarried(Packet250CustomPayload packet, Player senderPlayer) throws ClassNotFoundException, IOException
	{
		byte[] data = Utility.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		EntityPlayer player = (EntityPlayer)senderPlayer;
		World worldObj = player.worldObj;

		int playerId = (Integer) objectInput.readObject();
		String playerName = (String) objectInput.readObject();
		int spouseId = (Integer) objectInput.readObject();

		objectInput.close();

		//Workaround for problems on a server.
		String displayString = Localization.getString(player, null, "multiplayer.command.output.marry.accept", false, "\u00a7A", null);
		displayString = displayString.replace("%SpouseName%", playerName);
		player.addChatMessage(displayString);

		player.inventory.consumeInventoryItem(MCA.instance.itemWeddingRing.itemID);
	}

	private void handleHaveBaby(Packet250CustomPayload packet, Player senderPlayer) throws ClassNotFoundException, IOException
	{
		byte[] data = Utility.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		EntityPlayer player = (EntityPlayer)senderPlayer;
		World worldObj = player.worldObj;

		int playerId = (Integer) objectInput.readObject();
		int spouseId = (Integer) objectInput.readObject();

		objectInput.close();

		//Trigger the name baby gui.
		ItemBaby itemBaby = null;
		String babyGender = EntityBase.getRandomGender();

		if (babyGender.equals("Male"))
		{
			itemBaby = (ItemBaby)MCA.instance.itemBabyBoy;
			player.triggerAchievement(MCA.instance.achievementHaveBabyBoy);
			PacketDispatcher.sendPacketToServer(PacketCreator.createAchievementPacket(MCA.instance.achievementHaveBabyBoy, playerId));
			PacketDispatcher.sendPacketToServer(PacketCreator.createAchievementPacket(MCA.instance.achievementHaveBabyBoy, spouseId));
		}

		else if (babyGender.equals("Female"))
		{
			itemBaby = (ItemBaby)MCA.instance.itemBabyGirl;
			player.triggerAchievement(MCA.instance.achievementHaveBabyGirl);
			PacketDispatcher.sendPacketToServer(PacketCreator.createAchievementPacket(MCA.instance.achievementHaveBabyGirl, playerId));
			PacketDispatcher.sendPacketToServer(PacketCreator.createAchievementPacket(MCA.instance.achievementHaveBabyGirl, spouseId));
		}

		WorldPropertiesManager manager = MCA.instance.playerWorldManagerMap.get(player.username);
		manager.worldProperties.babyGender = babyGender;
		manager.worldProperties.babyExists = true;
		manager.saveWorldProperties();

		PacketDispatcher.sendPacketToServer(PacketCreator.createAddItemPacket(itemBaby.itemID, player.entityId));
		player.openGui(MCA.instance, MCA.instance.guiNameChildID, worldObj, (int)player.posX, (int)player.posY, (int)player.posZ);
	}

	private void handleBabyInfo(Packet250CustomPayload packet, Player senderPlayer) throws ClassNotFoundException, IOException
	{
		byte[] data = Utility.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		EntityPlayer player = (EntityPlayer)senderPlayer;
		World worldObj = player.worldObj;

		WorldPropertiesManager receivedManager = (WorldPropertiesManager) objectInput.readObject();

		objectInput.close();

		//Set the player's spouse's manager to have the same baby info.
		WorldPropertiesManager spouseManager = MCA.instance.playerWorldManagerMap.get(receivedManager.worldProperties.playerSpouseName);

		spouseManager.worldProperties.babyExists = receivedManager.worldProperties.babyExists;
		spouseManager.worldProperties.babyGender = receivedManager.worldProperties.babyGender;
		spouseManager.worldProperties.babyName = receivedManager.worldProperties.babyName;
		spouseManager.worldProperties.babyReadyToGrow = receivedManager.worldProperties.babyReadyToGrow;

		spouseManager.saveWorldProperties();
	}

	private void handleTrade(Packet250CustomPayload packet, Player senderPlayer) throws ClassNotFoundException, IOException
	{
		byte[] data = Utility.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		EntityPlayer player = (EntityPlayer)senderPlayer;
		World worldObj = player.worldObj;

		int entityId = (Integer)objectInput.readObject();

		objectInput.close();

		EntityVillagerAdult villager = (EntityVillagerAdult)worldObj.getEntityByID(entityId);
		villager.setCustomer(player);
		player.displayGUIMerchant(villager, villager.getTitle(MCA.instance.getIdOfPlayer(player), true));
	}

	private void handleRespawn(Packet250CustomPayload packet, Player senderPlayer) throws ClassNotFoundException, IOException, IllegalArgumentException, IllegalAccessException
	{
		byte[] data = Utility.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		EntityPlayerMP player = (EntityPlayerMP)senderPlayer;
		World worldObj = player.worldObj;
		MinecraftServer theServer = MinecraftServer.getServer();

		int chunkCoordsX = (Integer)objectInput.readObject();
		int chunkCoordsY = (Integer)objectInput.readObject();
		int chunkCoordsZ = (Integer)objectInput.readObject();
		int playerEntityId = (Integer)objectInput.readObject();

		objectInput.close();

		NetworkListenThread networkThread = theServer.getNetworkThread();

		//Get reference to the "connections" list, which is private final.
		for (Field f : networkThread.getClass().getSuperclass().getDeclaredFields())
		{
			if (f.getType().getName().equals("java.util.List"))
			{
				f.setAccessible(true);
				List connections = (List)f.get(networkThread);

				for (Object obj : connections)
				{
					NetServerHandler serverHandler = (NetServerHandler)obj;

					if (serverHandler.playerEntity.username.equals(player.username))
					{
						//Manually respawn the player rather than allowing the game to do it, which would delete the world in hardcore mode.
						serverHandler.playerEntity.setSpawnChunk(new ChunkCoordinates(chunkCoordsX, chunkCoordsY, chunkCoordsZ), true);
						serverHandler.playerEntity = theServer.getConfigurationManager().respawnPlayer(player, player.dimension, false);

						break;
					}
				}

				f.setAccessible(false);
				break;
			}
		}
	}

	private void handleVillagerPlayerProcreate(Packet250CustomPayload packet, Player senderPlayer) throws IOException, ClassNotFoundException
	{
		byte[] data = Utility.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		EntityPlayer player = (EntityPlayer)senderPlayer;
		World worldObj = player.worldObj;

		int villagerId = (Integer)objectInput.readObject();
		int playerId = (Integer)objectInput.readObject();
		String babyGender = (String)objectInput.readObject();

		objectInput.close();

		EntityVillagerAdult villager = (EntityVillagerAdult)worldObj.getEntityByID(villagerId);		
		ItemBaby itemBaby = null;

		//Give the villager an appropriate baby item and unlock achievements for the player.
		if (babyGender.equals("Male"))
		{
			itemBaby = (ItemBaby)MCA.instance.itemBabyBoy;
			villager.inventory.addItemStackToInventory(new ItemStack(itemBaby, 1));
			player.triggerAchievement(MCA.instance.achievementHaveBabyBoy);

			PacketDispatcher.sendPacketToServer(PacketCreator.createAchievementPacket(MCA.instance.achievementHaveBabyBoy, player.entityId));
			PacketDispatcher.sendPacketToServer(PacketCreator.createInventoryPacket(villagerId, villager.inventory));
		}

		else if (babyGender.equals("Female"))
		{
			itemBaby = (ItemBaby)MCA.instance.itemBabyGirl;
			villager.inventory.addItemStackToInventory(new ItemStack(itemBaby, 1));
			player.triggerAchievement(MCA.instance.achievementHaveBabyGirl);

			PacketDispatcher.sendPacketToServer(PacketCreator.createAchievementPacket(MCA.instance.achievementHaveBabyGirl, player.entityId));
			PacketDispatcher.sendPacketToServer(PacketCreator.createInventoryPacket(villagerId, villager.inventory));
		}

		//Modify the player's world properties manager.
		WorldPropertiesManager manager = MCA.instance.playerWorldManagerMap.get(player.username);
		manager.worldProperties.babyGender = babyGender;
		manager.worldProperties.babyExists = true;
		manager.saveWorldProperties();

		//Make the player choose a name for the baby.
		player.openGui(MCA.instance, MCA.instance.guiNameChildID, worldObj, (int)player.posX, (int)player.posY, (int)player.posZ);
	}
}
