package toufoumaster.btwaila.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiHudDesigner;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.hud.ComponentAnchor;
import net.minecraft.client.gui.hud.Layout;
import net.minecraft.client.gui.hud.MovableHudComponent;
import net.minecraft.core.HitResult;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.monster.EntityMonster;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import toufoumaster.btwaila.BTWailaClient;
import toufoumaster.btwaila.demo.DemoManager;
import toufoumaster.btwaila.mixin.interfaces.IOptions;
import toufoumaster.btwaila.util.Colors;

import static toufoumaster.btwaila.BTWaila.translator;

public class BaseInfoComponent extends MovableHudComponent {
    private final int topPadding = 4;
    private int ySize;
    public BaseInfoComponent(String key, Layout layout) {
        super(key, BTWailaClient.componentTextWidth, 24, layout);
    }

    @Override
    public int getAnchorY(ComponentAnchor anchor) {
        if (anchor.yPosition == 0.0f && !(anchor == ComponentAnchor.TOP_CENTER)){
            return (int)(anchor.yPosition * getYSize(Minecraft.getMinecraft(this))) + topPadding;
        }
        return (int)(anchor.yPosition * getYSize(Minecraft.getMinecraft(this)));
    }
    @Override
    public int getYSize(Minecraft mc) {
        if (!(mc.currentScreen instanceof GuiHudDesigner) && !this.isVisible(mc)) {
            return 0;
        }
        return this.ySize + topPadding;
    }
    @Override
    public boolean isVisible(Minecraft minecraft) {
        return minecraft.gameSettings.immersiveMode.drawHotbar();
    }

    @Override
    public void render(Minecraft minecraft, GuiIngame guiIngame, int xScreenSize, int yScreenSize, float f) {
        HitResult hitResult = minecraft.objectMouseOver;
        if (hitResult == null) {return;}
        if (hitResult.hitType == HitResult.HitType.TILE) {
            Block block = Block.getBlock(minecraft.theWorld.getBlockId(hitResult.x, hitResult.y, hitResult.z));
            int meta = minecraft.theWorld.getBlockMetadata(hitResult.x, hitResult.y, hitResult.z);
            ItemStack[] drops = block.getBreakResult(minecraft.theWorld, EnumDropCause.PICK_BLOCK, hitResult.x, hitResult.y, hitResult.z, minecraft.theWorld.getBlockMetadata(hitResult.x, hitResult.y, hitResult.z), null);
            baseBlockInfo(minecraft,block, meta, drops, xScreenSize, yScreenSize);
        } else if (hitResult.hitType == HitResult.HitType.ENTITY) {
            baseEntityInfo(minecraft, hitResult.entity, xScreenSize, yScreenSize);
        }
    }

    @Override
    public void renderPreview(Minecraft minecraft, Gui gui, Layout layout, int xScreenSize, int yScreenSize) {
        Block block = DemoManager.getCurrentEntry().block;
        int meta = DemoManager.getCurrentEntry().meta;
        ItemStack[] drops = DemoManager.getCurrentEntry().drops;
        Entity entity = DemoManager.getCurrentEntry().entity;
        if (block != null){
            baseBlockInfo(minecraft, block, meta, drops, xScreenSize, yScreenSize);
        } else if (entity != null) {
            baseEntityInfo(minecraft, entity, xScreenSize, yScreenSize);
        }
    }
    protected void baseBlockInfo(Minecraft minecraft, Block block, int blockMetadata, ItemStack[] blockDrops, int xScreenSize, int yScreenSize){
        int startY;
        int x = getLayout().getComponentX(minecraft, this, xScreenSize);
        int y = startY = getLayout().getComponentY(minecraft, this, yScreenSize) + topPadding;
        IOptions modSettings = (IOptions)minecraft.gameSettings;

        if (!modSettings.getBlockTooltips().value) return;
        if (minecraft.fontRenderer == null) return;

        ItemStack renderItem = new ItemStack(block, 1, blockMetadata);
        if (blockDrops != null && blockDrops.length > 0) renderItem = blockDrops[0];

        String languageKey = renderItem.getItemName();

        String blockName = translator.translateNameKey(languageKey);
        String blockDesc = translator.translateDescKey(languageKey);
        String blockSource = "Minecraft";
        for (String modId: BTWailaClient.modIds.keySet()){
            if (languageKey.contains(modId)){
                blockSource = BTWailaClient.modIds.get(modId);
            }
        }
        String idString = block.id + ":" + blockMetadata;
        if (modSettings.getShowBlockId().value){
            blockName += " " + idString;
        }

        y = drawStringJustified(minecraft,blockName, x, y,getXSize(minecraft), Colors.WHITE);
        y = drawStringJustified(minecraft,blockSource, x, y,getXSize(minecraft), Colors.BLUE);
        y = drawStringJustified(minecraft,blockDesc, x,y, getXSize(minecraft), Colors.LIGHT_GRAY);
        ySize = y - startY;
    }
    protected void baseEntityInfo(Minecraft minecraft, Entity entity, int xScreenSize, int yScreenSize){
        int startY;
        int x = getLayout().getComponentX(minecraft, this, xScreenSize);
        int y = startY = getLayout().getComponentY(minecraft, this, yScreenSize) + topPadding;
        IOptions gameSettings = (IOptions)minecraft.gameSettings;
        if (!gameSettings.getEntityTooltips().value) return;
        boolean isLivingEntity = (entity instanceof EntityLiving);
        EntityLiving entityLiving = isLivingEntity ? (EntityLiving) entity : null;

        int color = Colors.WHITE;
        if (isLivingEntity) {
            color = Colors.GREEN;
            if (entity instanceof EntityMonster) {
                color = Colors.RED;
            }
            else if (entity instanceof EntityPlayer) {
                color = entityLiving.chatColor;
            }
        }

        minecraft.fontRenderer.drawStringWithShadow(AdvancedInfoComponent.getEntityName(entity), x, y, color);
        y += BTWailaClient.getLineHeight();
        ySize = y - startY;
    }

    public int drawStringJustified(Minecraft minecraft,String text, int x, int y, int maxWidth, int color){
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        StringBuilder prevline;
        int wordCount = 0;
        for (String word: words) {
            prevline = new StringBuilder(line.toString());
            line.append(word).append(" ");
            wordCount++;
            if (minecraft.fontRenderer.getStringWidth(line.toString().trim()) > maxWidth){
                if (wordCount <= 1){
                    minecraft.fontRenderer.drawStringWithShadow(line.toString(), x, y, color);
                    y += BTWailaClient.getLineHeight();
                    line = new StringBuilder(word).append(" ");
                    wordCount = 0;
                    continue;
                }
                minecraft.fontRenderer.drawStringWithShadow(prevline.toString(), x, y, color);
                y += BTWailaClient.getLineHeight();
                line = new StringBuilder(word).append(" ");
                wordCount = 0;
            }
        }
        String remainder = line.toString();
        if (!remainder.isEmpty()){
            minecraft.fontRenderer.drawStringWithShadow(remainder, x, y, color);
            y += BTWailaClient.getLineHeight();
        }
        return y;
    }
}