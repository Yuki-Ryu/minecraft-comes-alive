/*******************************************************************************
 * GuiLostRelativeDocument.java
 * Copyright (c) 2013 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package mca;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.network.PacketDispatcher;

/**
 * Defines the GUI shown when the player gives someone a lost relative document.
 */
public class GuiLostRelativeDocument extends Gui
{
	private int originalGeneration;
	private int temporaryGeneration;
	private EntityBase recipient;
	private FamilyTree originalFamilyTree;
	private FamilyTree temporaryFamilyTree;
	
	private GuiButton buttonAunt;
	private GuiButton buttonBrother;
	private GuiButton buttonCousin;
	private GuiButton buttonFather;
	private GuiButton buttonGrandfather;
	private GuiButton buttonGreatgrandfather;
	private GuiButton buttonGrandmother;
	private GuiButton buttonGreatgrandmother;
	private GuiButton buttonMother;
	private GuiButton buttonNephew;
	private GuiButton buttonNiece;
	private GuiButton buttonSister;
	private GuiButton buttonUncle;
	private GuiButton buttonNone;
	
	private GuiButton buttonGeneration;
	private GuiButton buttonGenerationIncrease;
	private GuiButton buttonGenerationDecrease;
	
	private GuiButton buttonExit;
	
	private GuiButton buttonYes;
	private GuiButton buttonNo;
	
	private boolean inRelationSelectGui = false;
	private boolean inConfirmationGui = false;
	
	/**
	 * Constructor.
	 *
	 * @param 	player	The player that opened this GUI.
	 * @param	target	The target EntityBase.
	 */
	public GuiLostRelativeDocument(EntityPlayer player, EntityBase target) 
	{
		super(player);
		this.recipient = target;
		this.temporaryGeneration = target.generation;
		this.originalGeneration = target.generation;
		this.originalFamilyTree = target.familyTree.clone();
		this.temporaryFamilyTree = target.familyTree.clone();
	}

	@Override
	public void initGui()
	{
		drawLostRelativeDocumentGui();
	}
	
	@Override
	protected void actionPerformed(GuiButton guibutton)
	{
		String playerGender = MCA.instance.playerWorldManagerMap.get(player.username).worldProperties.playerGender;
		
		if (guibutton.enabled == false)
		{
			return;
		}
		
		else if (guibutton == buttonExit)
		{
			close();
			return;
		}
		
		else if (guibutton == buttonAunt)
		{
			if (playerGender.equals("Male"))
			{
				temporaryFamilyTree.addFamilyTreeEntry(player, EnumRelation.Nephew);
			}
			
			else
			{
				temporaryFamilyTree.addFamilyTreeEntry(player, EnumRelation.Niece);
			}
			
			drawConfirmationGui();
			return;
		}
		
		else if (guibutton == buttonBrother)
		{
			if (playerGender.equals("Male"))
			{
				temporaryFamilyTree.addFamilyTreeEntry(player, EnumRelation.Brother);
			}
			
			else
			{
				temporaryFamilyTree.addFamilyTreeEntry(player, EnumRelation.Sister);
			}
			
			drawConfirmationGui();
			return;
		}
		
		else if (guibutton == buttonCousin)
		{
			temporaryFamilyTree.addFamilyTreeEntry(player, EnumRelation.Cousin);
			drawConfirmationGui();
			return;
		}
		
		else if (guibutton == buttonFather)
		{
			if (playerGender.equals("Male"))
			{
				temporaryFamilyTree.addFamilyTreeEntry(player, EnumRelation.Son);
			}
			
			else
			{
				temporaryFamilyTree.addFamilyTreeEntry(player, EnumRelation.Daughter);
			}
			
			drawConfirmationGui();
			return;
		}
		
		else if (guibutton == buttonGrandfather)
		{
			if (playerGender.equals("Male"))
			{
				temporaryFamilyTree.addFamilyTreeEntry(player, EnumRelation.Grandson);
			}
			
			else
			{
				temporaryFamilyTree.addFamilyTreeEntry(player, EnumRelation.Granddaughter);
			}
			
			drawConfirmationGui();
			return;
		}
		
		else if (guibutton == buttonGreatgrandfather)
		{
			if (playerGender.equals("Male"))
			{
				temporaryFamilyTree.addFamilyTreeEntry(player, EnumRelation.Greatgrandson);
			}
			
			else
			{
				temporaryFamilyTree.addFamilyTreeEntry(player, EnumRelation.Greatgranddaughter);
			}
			
			drawConfirmationGui();
			return;
		}
		
		else if (guibutton == buttonGrandmother)
		{
			if (playerGender.equals("Male"))
			{
				temporaryFamilyTree.addFamilyTreeEntry(player, EnumRelation.Grandson);
			}
			
			else
			{
				temporaryFamilyTree.addFamilyTreeEntry(player, EnumRelation.Granddaughter);
			}
			
			drawConfirmationGui();
			return;
		}
		
		else if (guibutton == buttonGreatgrandmother)
		{
			if (playerGender.equals("Male"))
			{
				temporaryFamilyTree.addFamilyTreeEntry(player, EnumRelation.Greatgrandson);
			}
			
			else
			{
				temporaryFamilyTree.addFamilyTreeEntry(player, EnumRelation.Greatgranddaughter);
			}
			
			drawConfirmationGui();
			return;
		}
		
		else if (guibutton == buttonMother)
		{
			if (playerGender.equals("Male"))
			{
				temporaryFamilyTree.addFamilyTreeEntry(player, EnumRelation.Son);
			}
			
			else
			{
				temporaryFamilyTree.addFamilyTreeEntry(player, EnumRelation.Daughter);
			}
			
			drawConfirmationGui();
			return;
		}
		
		else if (guibutton == buttonNephew)
		{
			if (playerGender.equals("Male"))
			{
				temporaryFamilyTree.addFamilyTreeEntry(player, EnumRelation.Uncle);
			}
			
			else
			{
				temporaryFamilyTree.addFamilyTreeEntry(player, EnumRelation.Aunt);
			}
			
			drawConfirmationGui();
			return;
		}
		
		else if (guibutton == buttonNiece)
		{
			if (playerGender.equals("Male"))
			{
				temporaryFamilyTree.addFamilyTreeEntry(player, EnumRelation.Uncle);
			}
			
			else
			{
				temporaryFamilyTree.addFamilyTreeEntry(player, EnumRelation.Aunt);
			}
			
			drawConfirmationGui();
			return;
		}
		
		else if (guibutton == buttonSister)
		{
			if (playerGender.equals("Male"))
			{
				temporaryFamilyTree.addFamilyTreeEntry(player, EnumRelation.Brother);
			}
			
			else
			{
				temporaryFamilyTree.addFamilyTreeEntry(player, EnumRelation.Sister);
			}
			
			drawConfirmationGui();
			return;
		}
		
		else if (guibutton == buttonUncle)
		{
			if (playerGender.equals("Male"))
			{
				temporaryFamilyTree.addFamilyTreeEntry(player, EnumRelation.Nephew);
			}
			
			else
			{
				temporaryFamilyTree.addFamilyTreeEntry(player, EnumRelation.Niece);
			}
			
			drawConfirmationGui();
			return;
		}
		
		else if (guibutton == buttonNone)
		{
			temporaryFamilyTree.removeFamilyTreeEntry(player);
			drawConfirmationGui();
			return;
		}
		
		else if (guibutton == buttonGenerationIncrease)
		{
			temporaryGeneration++;
		}
		
		else if (guibutton == buttonGenerationDecrease)
		{
			temporaryGeneration--;
		}
		
		else if (guibutton == buttonGeneration)
		{
			temporaryGeneration = originalGeneration;
		}
		
		else if (guibutton == buttonNo)
		{
			recipient.familyTree = originalFamilyTree;
			PacketDispatcher.sendPacketToServer(PacketCreator.createSyncRequestPacket(recipient.entityId));
			drawLostRelativeDocumentGui();
			return;
		}
		
		else if (guibutton == buttonYes)
		{
			PacketDispatcher.sendPacketToServer(PacketCreator.createFamilyTreePacket(recipient.entityId, recipient.familyTree));
			
			EntityBase.removeItemFromPlayer(new ItemStack(MCA.instance.itemLostRelativeDocument, 1), player);
			
			if (recipient.familyTree.idIsRelative(MCA.instance.getIdOfPlayer(player)))
			{
				player.addChatMessage(Localization.getString(player, recipient, "notify.lostrelativedocument.success", false));
			}
			
			else
			{
				player.addChatMessage(Localization.getString(player, recipient, "notify.lostrelativedocument.success.norelation", false));
			}
			
			close();
			return;
		}
		
		drawLostRelativeDocumentGui();
	}
	
	@Override
	public void drawScreen(int sizeX, int sizeY, float offset)
	{
		drawDefaultBackground();

		if (inRelationSelectGui)
		{
			drawCenteredString(fontRenderer, Localization.getString(recipient, "gui.title.lostrelativedocument", false), width / 2, (height / 2) - 90, 0xFFFFFF);
			
			if (buttonMother != null)
			{
				buttonMother.enabled = true;
				buttonGrandmother.enabled = true;
				buttonGreatgrandmother.enabled = true;
				buttonSister.enabled = true;
				buttonAunt.enabled = true;
				buttonNiece.enabled = true;
				buttonCousin.enabled = true;
			}
			
			else
			{
				buttonFather.enabled = true;
				buttonGrandfather.enabled = true;
				buttonGreatgrandfather.enabled = true;
				buttonBrother.enabled = true;
				buttonUncle.enabled = true;
				buttonNephew.enabled = true;
				buttonCousin.enabled = true;
			}
			
			buttonNone.enabled = true;
			buttonGeneration.enabled = true;
			buttonGenerationIncrease.enabled = true;
			buttonGenerationDecrease.enabled = true;
		}
		
		else if (inConfirmationGui)
		{
			drawCenteredString(fontRenderer, Localization.getString(recipient, "gui.title.lostrelativedocument.confirm", false), width / 2, (height / 2) - 90, 0xFFFFFF);
			drawCenteredString(fontRenderer, recipient.getTitle(MCA.instance.getIdOfPlayer(player), false), width /2 , height / 2 - 70, 0xFFFFFF);
		}
		
		super.drawScreen(sizeX, sizeY, offset);
	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return true;
	}
	
	/**
	 * Draws the lost relative document GUI.
	 */
	private void drawLostRelativeDocumentGui()
	{
		inRelationSelectGui = true;
		inConfirmationGui = false;
		
		buttonList.clear();
		recipient.generation = temporaryGeneration;
		
		if (recipient.gender.equals("Male"))
		{
			buttonList.add(buttonFather = new GuiButton(1, width / 2 - 180, height / 2 - 40, 120, 20, Localization.getString("family.father.formal")));
			buttonList.add(buttonGrandfather = new GuiButton(2, width / 2 - 180, height / 2 - 20, 120, 20, Localization.getString("family.grandfather")));
			buttonList.add(buttonGreatgrandfather = new GuiButton(3, width / 2 - 180, height / 2, 120, 20, Localization.getString(recipient, "family.greatgrandfather", false)));
			buttonList.add(buttonBrother = new GuiButton(4, width / 2 - 60, height / 2 - 40, 120, 20, Localization.getString("family.brother")));
			buttonList.add(buttonUncle = new GuiButton(5, width / 2 - 60, height / 2 - 20, 120, 20, Localization.getString("family.uncle")));
			buttonList.add(buttonNephew = new GuiButton(6, width / 2 - 60, height / 2, 120, 20, Localization.getString("family.nephew")));
			buttonList.add(buttonCousin = new GuiButton(7, width / 2 + 60, height / 2 - 40, 120, 20, Localization.getString("family.cousin")));
			
			buttonFather.enabled = false;
			buttonGrandfather.enabled = false;
			buttonGreatgrandfather.enabled = false;
			buttonBrother.enabled = false;
			buttonUncle.enabled = false;
			buttonNephew.enabled = false;
			buttonCousin.enabled = false;
		}
		
		else if (recipient.gender.equals("Female"))
		{
			buttonList.add(buttonMother = new GuiButton(1, width / 2 - 180, height / 2 - 40, 120, 20, Localization.getString("family.mother.formal")));
			buttonList.add(buttonGrandmother = new GuiButton(2, width / 2 - 180, height / 2 - 20, 120, 20, Localization.getString("family.grandmother")));
			buttonList.add(buttonGreatgrandmother = new GuiButton(3, width / 2 - 180, height / 2, 120, 20, Localization.getString(recipient, "family.greatgrandmother", false)));
			buttonList.add(buttonSister = new GuiButton(4, width / 2 - 60, height / 2 - 40, 120, 20, Localization.getString("family.sister")));
			buttonList.add(buttonAunt = new GuiButton(5, width / 2 - 60, height / 2 - 20, 120, 20, Localization.getString("family.aunt")));
			buttonList.add(buttonNiece = new GuiButton(6, width / 2 - 60, height / 2, 120, 20, Localization.getString("family.niece")));
			buttonList.add(buttonCousin = new GuiButton(7, width / 2 + 60, height / 2 - 40, 120, 20, Localization.getString("family.cousin")));
			
			buttonMother.enabled = false;
			buttonGrandmother.enabled = false;
			buttonGreatgrandmother.enabled = false;
			buttonSister.enabled = false;
			buttonAunt.enabled = false;
			buttonNiece.enabled = false;
			buttonCousin.enabled = false;
		}
		
		buttonList.add(buttonNone = new GuiButton(8, width / 2 + 60, height / 2 - 20, 120, 20, Localization.getString("family.none")));
		buttonList.add(buttonGeneration = new GuiButton(9, width / 2 - 60, height / 2 + 35, 120, 20, Localization.getString(recipient, "gui.button.lostrelativedocument.generation", false)));
		buttonList.add(buttonGenerationIncrease = new GuiButton(10, width / 2 + 60, height / 2 + 35, 20, 20, "+"));
		buttonList.add(buttonGenerationDecrease = new GuiButton(11, width / 2 - 80, height / 2 + 35, 20, 20, "-"));
		
		buttonNone.enabled = false;
		buttonGeneration.enabled = false;
		buttonGenerationIncrease.enabled = false;
		buttonGenerationDecrease.enabled = false;
		
		buttonList.add(buttonExit = new GuiButton(0, width / 2 - 50, height / 2 + 75, 100, 20, Localization.getString("gui.button.exit")));
		
		recipient.generation = originalGeneration;
	}
	
	/**
	 * Draws the GUI that confirms that the player wants the recipient to have the selected relation to them.
	 */
	private void drawConfirmationGui()
	{
		temporaryFamilyTree.dumpTreeContents();
		originalFamilyTree.dumpTreeContents();
		
		recipient.familyTree = temporaryFamilyTree;
		
		inConfirmationGui = true;
		inRelationSelectGui = false;
	
		buttonList.clear();
		
		buttonList.add(buttonYes = new GuiButton(1, width / 2 - 80, height / 2 + 10, 60, 20, Localization.getString("gui.button.yes")));
		buttonList.add(buttonNo = new GuiButton(2, width / 2 + 30, height / 2 + 10, 60, 20, Localization.getString("gui.button.no")));
	}
}
