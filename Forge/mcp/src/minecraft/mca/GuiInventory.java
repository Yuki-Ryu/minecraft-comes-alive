/*******************************************************************************
 * GuiInventory.java
 * Copyright (c) 2013 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package mca;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.inventory.IInventory;
import net.minecraft.src.ModLoader;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Defines the inventory GUI shown when changing an entity's inventory.
 */
@SideOnly(Side.CLIENT)
public class GuiInventory extends InventoryEffectRenderer
{	
	private static final ResourceLocation resourceLocation = new ResourceLocation("textures/gui/container/generic_54.png");

	private EntityBase owner;
	private IInventory playerInventory;
	private IInventory entityInventory;
	private GuiButton backButton;
	private GuiButton exitButton;

	/** The number of rows in the inventory. */
	private int inventoryRows;

	/** Has the inventory been opened from the villager editor? */
	private boolean fromEditor = false;

	/**
	 * Constructor
	 * 
	 * @param 	entity			The entity who owns the inventory being accessed.
	 * @param 	playerInventory	The inventory of the player opening this GUI.
	 * @param 	entityInventory	The inventory of the entity that the player is interacting with.
	 * @param	fromEditor		Is this GUI being opened from the villager editor?
	 */
	public GuiInventory(EntityBase entity, IInventory playerInventory, IInventory entityInventory, boolean fromEditor)
	{
		super(new ContainerInventory(playerInventory, entityInventory));

		owner = entity;
		allowUserInput = false;

		this.playerInventory = playerInventory;
		this.entityInventory = entityInventory;
		this.fromEditor = fromEditor;

		char c = '\336';
		int i = c - 108;		
		inventoryRows = entityInventory.getSizeInventory() / 9;
		ySize = i + inventoryRows * 18;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		buttonList.clear();
		buttonList.add(backButton = new GuiButton(10, width / 2 - 190, height / 2 + 85, 65, 20, Localization.getString("gui.button.back")));
		buttonList.add(exitButton = new GuiButton(11, width / 2 + 125, height / 2 + 85, 65, 20, Localization.getString("gui.button.exit")));
	}

	@Override
	protected void actionPerformed(GuiButton guibutton)
	{
		if (guibutton == backButton)
		{
			if (!fromEditor)
			{
				if (owner instanceof EntityPlayerChild)
				{
					Minecraft.getMinecraft().displayGuiScreen(new GuiInteractionPlayerChild((EntityPlayerChild)owner, owner.worldObj.getPlayerEntityByName(owner.lastInteractingPlayer)));
				}

				else if (owner instanceof EntityVillagerAdult)
				{
					Minecraft.getMinecraft().displayGuiScreen(new GuiInteractionSpouse((EntityVillagerAdult)owner, owner.worldObj.getPlayerEntityByName(owner.lastInteractingPlayer)));
				}
			}

			else
			{
				ModLoader.openGUI(Minecraft.getMinecraft().thePlayer, new GuiVillagerEditor(owner, Minecraft.getMinecraft().thePlayer));
			}
		}

		else if (guibutton == exitButton)
		{
			if (!fromEditor)
			{
				Minecraft.getMinecraft().displayGuiScreen(null);
			}

			else
			{
				ModLoader.openGUI(Minecraft.getMinecraft().thePlayer, new GuiVillagerEditor(owner, Minecraft.getMinecraft().thePlayer));
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRenderer.drawString(Localization.getString(owner, "gui.title.inventory", false), 7, 6, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float offset, int mouseX, int mouseY)
	{
		this.mc.func_110434_K().func_110577_a(resourceLocation);

		int addX = Minecraft.getMinecraft().thePlayer.getActivePotionEffects().size() > 0 ? 120 : 0;

		//Draw the two inventories.
		int x = (width - xSize + addX) / 2;
		int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, inventoryRows * 18 + 17);			//Top inventory
		drawTexturedModalRect(x, y + inventoryRows * 18 + 17, 0, 126, xSize, 96);	//Bottom inventory
	}

	@Override
	public void onGuiClosed() 
	{
		super.onGuiClosed();
		PacketDispatcher.sendPacketToServer(PacketCreator.createInventoryPacket(owner.entityId, owner.inventory));
	}
}
