package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;

public class mod_IFB_FrenchBread extends BaseMod {

	@MLProp(info="Item's ID.(ShiftedIndex) +0 .. +1", max=32000)
	public static int ItemID = 22205;
	@MLProp(info="use Icon.(false is bread icon)")
	public static boolean isUseIcon = true;
	@MLProp(info="enable TATHUJIN mode.")
	public static boolean isTathujin = true;
	@MLProp(info="Check GrassBlock.")
	public static boolean isGrassBlock = true;
	@MLProp(info="Leaves BlockID.")
	public static String additionalLeaveBlockIDs = "18";

	// ��V�p����ʂɂ��Ă���̂̓X�^�b�N�΍�
	public static Item frenchbread;
	public static Item druggedfrenchbread;
	public static List<Integer> leavesBlockIDs = new ArrayList<Integer>();  
	public static Minecraft mc = null;


	@Override
	public String getVersion() {
		return "1.4.6-1";
	}

	@Override
	public String getName() {
		return "FrenchBread";
	}
	
	@Override
	public String getPriorities() {
		return "required-after:mod_MMM_MMMLib";
	}
	
	@Override
	public void load() {
		try {
			mc = ModLoader.getMinecraftInstance();
		} catch (Error e) {
		}

		// �A�C�e���̒ǉ�
		int icon = Item.bread.iconIndex;
		if (isUseIcon && mc != null) {
			icon = MMM_Helper.isForge ? 33 : ModLoader.addOverride("/gui/items.png", "/icon/frenchBread.png");
		}
		frenchbread = new IFB_ItemFrenchBread(ItemID - 256).setIconIndex(icon).setItemName("FrenchBread");
		druggedfrenchbread = new IFB_ItemFrenchBread(ItemID + 1 - 256).setIconIndex(icon).setItemName("FrenchBreadDrugged");
		MMM_Helper.setForgeIcon(frenchbread);
		MMM_Helper.setForgeIcon(druggedfrenchbread);
		// ���O
		ModLoader.addName(frenchbread, "French Bread");
		ModLoader.addName(frenchbread, "ja_JP", "�t�����X�p��");
		ModLoader.addName(druggedfrenchbread, "Drugged French Bread");
		ModLoader.addName(druggedfrenchbread, "ja_JP", "��V�t�����X�p��");
		
		// �ʏ탌�V�s
		ModLoader.addRecipe(new ItemStack(frenchbread), new Object[] {
			"b", "b", "b",
			Character.valueOf('b'), Item.bread});
		// ��Ђ����V�s
		CraftingManager.getInstance().getRecipeList().add(new IFB_RecipesFrenchBread());

		// �t���σ��X�g�̍\�z
		String[] s = additionalLeaveBlockIDs.split(",");
		for (String t : s) {
			Integer iid = Integer.valueOf(t.trim());
			if (!leavesBlockIDs.contains(iid)) {
				leavesBlockIDs.add(iid);
			}
		}

	}

	@Override
	public void takenFromCrafting(EntityPlayer var1, ItemStack var2, IInventory var3) {
		// ��{�g����Ԃ�
		for (int li = 0; li < var3.getSizeInventory(); li++) {
			ItemStack lis = var3.getStackInSlot(li);
			if (lis != null && lis.getItem() instanceof ItemPotion) {
				lis = new ItemStack(Item.glassBottle);
				if (!var1.inventory.addItemStackToInventory(lis)) {
					var1.dropPlayerItem(lis);
				}
			}
		}
	}

}