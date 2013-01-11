package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import net.minecraft.client.Minecraft;

public class IFB_ItemFrenchBread extends ItemFood {

	private float saturationModifier;
	private int weaponDamage;


	public IFB_ItemFrenchBread(int i) {
		super(i, 10, 0.6F, false);
		maxStackSize = 1;
		setMaxDamage(EnumToolMaterial.GOLD.getMaxUses() / 2);
		// �U���͂͋��_�C���\�[�h����
		weaponDamage = 4 + EnumToolMaterial.EMERALD.getDamageVsEntity() * 2;
		saturationModifier = super.getSaturationModifier();
	}

	@Override
	public ItemStack onFoodEaten(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		float rotten = ((float)itemstack.getItemDamage() / (float)getMaxDamage());
		if (rotten > 0.2F) {
			// �ɂ񂾃p��
			if(!world.isRemote && world.rand.nextFloat() < rotten) {
				entityplayer.addPotionEffect(new PotionEffect(Potion.hunger.id, 30 * 20, 0));
			}
		}
		// �h�[�s���O�t�����X�p��
		if (!entityplayer.worldObj.isRemote) {
			addPotionEffect(entityplayer, null, itemstack);
		}
		
		// ������
		saturationModifier = 0.6F * (1.0F - rotten);
		itemstack = super.onFoodEaten(itemstack, world, entityplayer);
		
		return itemstack;
	}

	@Override
	public float getStrVsBlock(ItemStack itemstack, Block block) {
		return 15F;
	}

	public void addPotionEffect(EntityLiving pTarget, EntityLiving pAttaker, ItemStack pItemStack) {
		// �|�[�V�����̌��ʂ��^�[�Q�b�g�֓��^
		int[] el = getDruggedEffects(pItemStack);
		if (el != null) {
			for (int li = 0; li < el.length; li++) {
				if (el[li] == 0) continue;
				List list1 = Item.potion.getEffects(el[li]);
				if(list1 != null) {
					for (int lj = 0; lj < list1.size(); lj++) {
						PotionEffect potioneffect = (PotionEffect)list1.get(lj);
						int lpid = potioneffect.getPotionID();

						if (Potion.potionTypes[lpid].isInstant()) {
							// �_���|�̌��ʂ��������Z
							pTarget.hurtResistantTime = 0;
							Potion.potionTypes[lpid].affectEntity(pAttaker, pTarget, potioneffect.getAmplifier(), 1.0F);
							pTarget.hurtResistantTime = 0;
						} else {
							pTarget.addPotionEffect(new PotionEffect(potioneffect));
						}
					}
				}
			}
		}
	}

	@Override
	public boolean hitEntity(ItemStack itemstack, EntityLiving entityliving, EntityLiving entityliving1) {
		// �����h�[�s���O
		if (!entityliving1.worldObj.isRemote) {
			addPotionEffect(entityliving, entityliving1, itemstack);
		}
		// �G��Ă�Ə��Ց���
		int damage = (entityliving.isWet() || entityliving1.isWet()) ? 4 : 1; 
		itemstack.damageItem(damage, entityliving1);

		return true;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack par1ItemStack, World par2World,
			int par3, int par4, int par5, int par6, EntityLiving par7EntityLiving) {
		int damage = par7EntityLiving.isWet() ? 8 : 2;
		par1ItemStack.damageItem(damage, par7EntityLiving);
		if (mod_IFB_FrenchBread.isTathujin) {
			// �y�䔻��
			World world1 = par7EntityLiving.worldObj;
			int baseblockid = world1.getBlockId(par4, par5 - 1, par6);
			if (par5 > 0 && 
					(Block.blocksList[par3] instanceof BlockLog || Block.blocksList[par3] instanceof BlockMushroomCap) &&
					(baseblockid == Block.dirt.blockID || (baseblockid == Block.grass.blockID && mod_IFB_FrenchBread.isGrassBlock)) && 
					world1.getBlockId(par4, par5 + 1, par6) == par3) {
				// ���̎��_�Ŋ��Ƀu���b�N�͔j�󂳂�Ă���̂Ń��^�f�[�^�����Ȃ�
				int metadata = world1.getBlockMetadata(par4, par5 + 1, par6);
				checkTATHUJIN(par1ItemStack, par3, par4, par5, par6, metadata, par7EntityLiving, par3, metadata, 0);
			}
		}
		return true;
	}

	@Override
	public int getDamageVsEntity(Entity entity) {
		return weaponDamage;
	}

	@Override
	public boolean isFull3D() {
		return true;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		return isGuard(itemstack) ? 0x11940 : super.getMaxItemUseDuration(itemstack);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		return isGuard(itemstack) ? EnumAction.block : super.getItemUseAction(itemstack);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		// �K�[�h����
		if (!entityplayer.foodStats.needFood()) {
			setGuard(itemstack, true, entityplayer);
			entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
			return itemstack;
		} else {
			setGuard(itemstack, false, entityplayer);
			return super.onItemRightClick(itemstack, world, entityplayer);
		}
	}

	@Override
	public boolean canHarvestBlock(Block block) {
		// �ʏ�j��ł͉��҂�������͂ł��Ȃ�
		return false;
	}

	@Override
	public float getSaturationModifier() {
		// �������ݒ�
		return saturationModifier;
	}

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
		// ���ʕ�����̒ǉ�
		if (!hasEffect(itemstack)) {
			return;
		}
		int[] el = getDruggedEffects(itemstack);
		if (el != null) {
			for (int i = 0; i < el.length; i++) {
				if (el[i] == 0) continue;
				List list1 = Item.potion.getEffects(el[i]);
				if (list1 != null && !list1.isEmpty()) {
					for (Iterator iterator = list1.iterator(); iterator.hasNext();) {
						PotionEffect potioneffect = (PotionEffect)iterator.next();
						String s1 = StatCollector.translateToLocal(potioneffect.getEffectName()).trim();
						if (potioneffect.getAmplifier() > 0) {
							s1 = (new StringBuilder()).append(s1).append(" ").append(StatCollector.translateToLocal((new StringBuilder()).append("potion.potency.").append(potioneffect.getAmplifier()).toString()).trim()).toString();
						}
						if (potioneffect.getDuration() > 20) {
							s1 = (new StringBuilder()).append(s1).append(" (").append(Potion.getDurationString(potioneffect)).append(")").toString();
						}
						if (Potion.potionTypes[potioneffect.getPotionID()].isBadEffect()) {
							list.add((new StringBuilder()).append("\247c").append(s1).toString());
						} else {
							list.add((new StringBuilder()).append("\2477").append(s1).toString());
						}
					}
				} else {
					String s = StatCollector.translateToLocal("potion.empty").trim();
					list.add((new StringBuilder()).append("\2477").append(s).toString());
				}
			}
		}
	}

	@Override
	public boolean hasEffect(ItemStack itemstack) {
		// �h�[�s���O�\��
		return itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("potions");
	}



	public static void checkTATHUJIN(ItemStack itemstack, int blockidOrig, int j, int k, int l, int metadataOrig, EntityLiving entityliving, int blockidTarget, int metadataTarget, int count) {
		World world1 = entityliving.worldObj;
		// �͈͔���
		if (count > 5) return;
		// �B�l�͈ꑾ���ő�؂����؂蕥���Ƃ���
		if (world1.setBlockWithNotify(j, k, l, 0)) {
			itemstack.damageItem(1, entityliving);
			// �A�C�e���̃h���b�v
			Block bb = Block.blocksList[blockidTarget];
			bb.dropBlockAsItem_do(world1, j, k, l, new ItemStack(blockidTarget, 1, bb.damageDropped(metadataTarget)));
		}
		for (int y = (Block.blocksList[blockidTarget] instanceof BlockLog) ? 0 : -1; y < 2; y ++) {
			for (int z = -1; z < 2; z++) {
				for (int x = -1; x < 2; x++) {
					int blockid = world1.getBlockId(j + x, k + y, l + z);
					if (blockid == blockidTarget || (Block.blocksList[blockidOrig] instanceof BlockLog && Block.blocksList[blockid] instanceof BlockLeavesBase)) {
						int blockmeta = world1.getBlockMetadata(j + x, k + y, l + z);
						int lcount = count;
						if (mod_IFB_FrenchBread.leavesBlockIDs.contains(blockid)) {
							blockmeta &= 0x03;
							lcount++;
						}
						else if (Block.blocksList[blockid] instanceof BlockMushroomCap) blockmeta = metadataOrig;
						if (blockmeta == metadataOrig) {
							checkTATHUJIN(itemstack, blockidOrig, j + x, k + y, l + z, metadataOrig, entityliving, blockid, blockmeta, lcount);
						}
					}
				}
			}
		}
	}

	protected static boolean isGuard(ItemStack pitemstack) {
		// �h��H
		if (pitemstack.hasTagCompound()) {
			return pitemstack.getTagCompound().getBoolean("isGuard");
		} else {
			return false;
		}
	}

	protected static void setGuard(ItemStack pitemstack, boolean pmode, EntityPlayer pplayer) {
		// �H�ׂ邩�h�䂩�̐ݒ�
		if (!pitemstack.hasTagCompound()) {
			pitemstack.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound lnbt = pitemstack.getTagCompound();
		if (lnbt.getBoolean("isGuard") != pmode) {
			// �ݒ�l�ƈႤ�ꍇ�͒l���Z�b�g
			lnbt.setBoolean("isGuard", pmode);
		}
		
		MMM_Helper.updateCheckinghSlot(pplayer, pitemstack);
	}


	public static void setDruggedEffects(ItemStack itemstack, int[] eff) {
		// �h�[�s���O���ʂ�ݒ�
		if (!itemstack.hasTagCompound()) {
			itemstack.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound nbttagcompound1 = itemstack.getTagCompound();
		if (nbttagcompound1 == null) {
			return;
		}
		if(!nbttagcompound1.hasKey("potions")) {
			nbttagcompound1.setTag("potions", new NBTTagCompound("potions"));
		}
		NBTTagCompound nbttagcompound2 = nbttagcompound1.getCompoundTag("potions");
		for (int i = 0; i < eff.length; i++) {
			nbttagcompound2.setInteger(Integer.toString(i), eff[i]);
		}
	}

	public static int[] getDruggedEffects(ItemStack itemstack) {
		// �h�[�s���O���ʂ����o��
		int eff[] = {0, 0, 0, 0, 0, 0};

		if (!itemstack.hasTagCompound()) {
			return null;
		}
		NBTTagCompound nbttagcompound1 = itemstack.getTagCompound();
		if(nbttagcompound1 == null || !nbttagcompound1.hasKey("potions")) {
			return null;
		}
		NBTTagCompound nbttagcompound2 = nbttagcompound1.getCompoundTag("potions");
		for (int i = 0; i < eff.length; i++) {
			eff[i] = nbttagcompound2.getInteger(Integer.toString(i));
		}
		return eff;
	}

}