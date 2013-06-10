package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

public class IFB_RecipesFrenchBread implements IRecipe {

	private ItemStack fItem = new ItemStack(mod_IFB_FrenchBread.druggedfrenchbread);


	@Override
	public boolean matches(InventoryCrafting var1, World var2) {
		int lcount = 0;
		int ldust = 0;
		for (int li = 0; li < 3; li++) {
			int lbread = 0;
			for (int lj = 0; lj < 3; lj++) {
				ItemStack lis = var1.getStackInRowAndColumn(li, lj);
				if (lis != null && lis.itemID == Item.bread.itemID) {
					lbread++;
					continue;
				}
				if (lis != null && !(lis.getItem() instanceof ItemPotion)) {
					ldust++;
				}
			}
			if (lbread == 3) {
				lcount++;
			}
		}
		if (lcount == 1 && ldust == 0) {
			return true;
		}
		return false;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting var1) {
		int lpe[] = {0, 0, 0, 0, 0, 0};
		int lj = 0;
		
		for (int li = 0; li < var1.getSizeInventory() && lj < 6; li++) {
			ItemStack ldrag = var1.getStackInSlot(li);
			if (ldrag != null && ldrag.getItem() instanceof ItemPotion && MMM_Helper.hasEffect(ldrag)) {
				lpe[lj++] = ldrag.getItemDamage();
			}
		}
		if (lj > 0) {
			ItemStack lis = fItem.copy();
			IFB_ItemFrenchBread.setDruggedEffects(lis, lpe);
			return lis;
		}
		
		return null;
	}

	@Override
	public int getRecipeSize() {
		return 9;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return fItem;
	}

}
