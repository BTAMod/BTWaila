package toufoumaster.btwaila.tooltips.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityTrommel;
import net.minecraft.core.util.helper.Side;
import toufoumaster.btwaila.BTWaila;
import toufoumaster.btwaila.IBTWailaCustomBlockTooltip;
import toufoumaster.btwaila.TooltipGroup;
import toufoumaster.btwaila.TooltipRegistry;
import toufoumaster.btwaila.gui.GuiBlockOverlay;
import toufoumaster.btwaila.util.Colors;
import toufoumaster.btwaila.util.ProgressBarOptions;
import toufoumaster.btwaila.util.TextureOptions;

public class TrommelTooltip implements IBTWailaCustomBlockTooltip {

    @Override
    public void addTooltip() {
        BTWaila.LOGGER.info("Adding tooltips for: " + this.getClass().getSimpleName());
        TooltipGroup tooltipGroup = new TooltipGroup("minecraft", TileEntityTrommel.class, this);
        tooltipGroup.addTooltip(TileEntityTrommel.class);
        TooltipRegistry.tooltipMap.add(tooltipGroup);
    }

    @Override
    public void drawAdvancedTooltip(TileEntity tileEntity, GuiBlockOverlay guiBlockOverlay) {
        TileEntityTrommel trommel = (TileEntityTrommel) tileEntity;

        ProgressBarOptions options = new ProgressBarOptions().setText("Progress: ");
        guiBlockOverlay.drawProgressBarWithText((int) trommel.getCookProgressPercent(100), 100, options, 32);

        int value = (int) trommel.getCookProgressPercent(15000);
        int max = 15000;
        ProgressBarOptions options2 = new ProgressBarOptions().setText("Lava: "+value+"/"+max+"mb").setBoxWidth(150).setValues(false).setPercentage(false);
        TextureOptions bgOpt = new TextureOptions().setBlockId(Block.fluidLavaStill.id).setColor(Colors.GRAY);
        TextureOptions fgOpt = new TextureOptions().setBlockId(Block.fluidLavaStill.id).setColor(Colors.LIGHT_GRAY);
        options2.setBackgroundOptions(bgOpt).setForegroundOptions(fgOpt);
        guiBlockOverlay.drawProgressBarTextureWithText(value, max, options2, 32);
    }
}
