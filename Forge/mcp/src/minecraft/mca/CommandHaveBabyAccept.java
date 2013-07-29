/*******************************************************************************
 * CommandHaveBabyAccept.java
 * Copyright (c) 2013 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package mca;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

/**
 * Handles the marriage acceptance command.
 */
public class CommandHaveBabyAccept extends Command
{
	@Override
	public String getCommandUsage(ICommandSender sender) 
	{
		return "/mca.havebaby.accept";
	}

	@Override
	public String getCommandName() 
	{
		return "mca.havebaby.accept";
	}

	@SuppressWarnings("unused")
	@Override
	public void processCommand(ICommandSender sender, String[] arguments) 
	{
		//Make sure they are married to a player.
		EntityPlayer player = MCA.instance.getPlayerByName(sender.getCommandSenderName());
		WorldPropertiesManager senderManager = MCA.instance.playerWorldManagerMap.get(sender.getCommandSenderName());

		if (senderManager.worldProperties.playerSpouseID < 0)
		{
			//Check if the spouse is on the server.
			EntityPlayer spouse = MCA.instance.getPlayerByName(senderManager.worldProperties.playerSpouseName);
			WorldPropertiesManager spouseManager = MCA.instance.playerWorldManagerMap.get(spouse.username);

			if (spouse != null)
			{
				//Make sure they were asked.
				if (MCA.instance.babyRequests.get(spouse.username).equals(sender.getCommandSenderName()))
				{
					//Notify the other that they want to have a baby and tell the server they have asked.
					this.sendChatToPlayer(spouse, "multiplayer.command.output.havebaby.successful", GREEN, null);
					PacketDispatcher.sendPacketToPlayer(PacketCreator.createHaveBabyPacket(spouse.entityId, player.entityId), (Player)spouse);
					
					//And remove their entry from the map.
					MCA.instance.babyRequests.remove(sender.getCommandSenderName());
					MCA.instance.babyRequests.remove(spouse.username);
				}
				
				else
				{
					this.sendChatToPlayer(sender, "multiplayer.command.output.havebaby.failed.notasked", RED, null);
				}
			}

			//The spouse is not on the server.
			else
			{
				this.sendChatToPlayer(sender, "multiplayer.command.output.havebaby.failed.offline", RED, null);
			}
		}

		//The sender is not married to a player.
		else
		{
			//This phrase works for this situation as well. No need for duplicate entries.
			this.sendChatToPlayer(sender, "multiplayer.command.output.divorce.failed.notmarried", RED, null);
		}
	}
}
