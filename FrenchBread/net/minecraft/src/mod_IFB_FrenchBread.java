package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;

public class mod_IFB_FrenchBread extends BaseMod {

	@MLProp(info="Item's ID.(ShiftedIndex) +0 .. +1", max=32000)
	public static int ItemID = 22205;
	@MLProp(info="enable TATHUJIN mode.")
	public static boolean isTathujin = true;
	@MLProp(info="Check GrassBlock.")
	public static boolean isGrassBlock = true;
	@MLProp(info="Leaves BlockID.")
	public static String additionalLeaveBlockIDs = "18";

	// 薬膳パンを別にしているのはスタック対策
	public static Item frenchbread;
	public static Item druggedfrenchbread;
	public static List<Integer> leavesBlockIDs = new ArrayList<Integer>();  


	@Override
	public String getVersion() {
		return "1.5.2-1";
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
		// MMMLibのRevisionチェック
		MMM_Helper.checkRevision("1");
		
		// アイテムの追加
		frenchbread = (new IFB_ItemFrenchBread(ItemID - 256, false)).setUnlocalizedName("FrenchBread");
		druggedfrenchbread = (new IFB_ItemFrenchBread(ItemID + 1 - 256, true)).setUnlocalizedName("FrenchBreadDrugged");
		// 名前
		ModLoader.addName(frenchbread, "French Bread");
		ModLoader.addName(frenchbread, "ja_JP", "フランスパン");
		ModLoader.addName(druggedfrenchbread, "Drugged French Bread");
		ModLoader.addName(druggedfrenchbread, "ja_JP", "薬膳フランスパン");
		
		// 通常レシピ
		ModLoader.addRecipe(new ItemStack(frenchbread), new Object[] {
			"b", "b", "b",
			Character.valueOf('b'), Item.bread});
		// 薬漬けレシピ
		CraftingManager.getInstance().getRecipeList().add(new IFB_RecipesFrenchBread());
		
		// 葉っぱリストの構築
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
		// 空ボトルを返す
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
