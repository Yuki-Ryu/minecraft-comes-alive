/*******************************************************************************
 * CommandMarryDecline.java
 * Copyright (c) 2013 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package mca;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

/**
 * Handles the marriage decline command.
 */
public class CommandMarryDecline extends Command 
{
	@Override
	public String getCommandUsage(ICommandSender sender) 
	{
		return "/mca.marry.decline <PLAYERNAME>";
	}

	@Override
	public String getCommandName() 
	{
		return "mca.marry.decline";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] arguments) 
	{
		//Find the sender.
		EntityPlayer player = null;

		for (WorldServer world : MinecraftServer.getServer().worldServers)
		{
			if (world.getPlayerEntityByName(sender.getCommandSenderName()) != null)
			{
				player = world.getPlayerEntityByName(sender.getCommandSenderName());
				break;
			}
		}

		if (arguments.length == 1)
		{
			//Ensure that the provided player has asked to marry this person.
			String mostRecentPlayerAsked = MCA.instance.marriageRequests.get(arguments[0]);

			if (mostRecentPlayerAsked == null)
			{
				super.sendChatToPlayer(sender, "multiplayer.command.output.marry.norequest", RED, null);
			}

			else
			{
				if (mostRecentPlayerAsked.equals(sender.getCommandSenderName()))
				{
					//Make sure the recipient is on the server.
					EntityPlayer recipient = null;

					for (WorldServer world : MinecraftServer.getServer().worldServers)
					{
						if (world.getPlayerEntityByName(arguments[0]) != null)
						{
							recipient = world.getPlayerEntityByName(arguments[0]);
							break;
						}
					}

					if (recipient != null)
					{
						//Notify the recipient that the other player declined.
						super.sendChatToOtherPlayer(sender, (EntityPlayer)recipient, "multiplayer.command.output.marry.decline", null, null);
						MCA.instance.marriageRequests.remove(sender.getCommandSenderName());
					}

					else
					{
						super.sendChatToPlayer(sender, "multiplayer.command.error.playeroffline", RED, null);
					}
				}

				else
				{
					super.sendChatToPlayer(sender, "multiplayer.command.output.marry.norequest", RED, null);
				}
			}
		}

		else
		{
			super.sendChatToPlayer(sender, "multiplayer.command.error.parameter", RED, null);
		}
	}
}
