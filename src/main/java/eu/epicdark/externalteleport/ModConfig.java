package eu.epicdark.externalteleport;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = ExternalTeleport.MOD_ID)
public class ModConfig implements ConfigData {
    public enum RenderState {
        INVISIBLE,
        TRANSPARENT,
        FULL;
    }


    @Comment("Command to execute when teleporting; Can contain $x $y $z $yaw $pitch")
    @ConfigEntry.Gui.Tooltip
    public String command = "tp @s $x $y $z $yaw $pitch";
    @ConfigEntry.Gui.Tooltip
    public boolean playSound = true;

    public boolean showHand = false;
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public RenderState showPlayer = RenderState.TRANSPARENT;


}
