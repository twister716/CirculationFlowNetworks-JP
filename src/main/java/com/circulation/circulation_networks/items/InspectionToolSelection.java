package com.circulation.circulation_networks.items;

import com.circulation.circulation_networks.items.InspectionToolModeModel.ToolFunction;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
//? if <1.20 {
import net.minecraft.item.ItemStack;
//?} else {
/*import net.minecraft.world.item.ItemStack;
*///?}

public final class InspectionToolSelection {

    public static final String MODE_DISPLAY_KEY = "item.circulation_networks.inspection_tool.mode_display";
    public static final String CURRENT_MODE_KEY = "item.circulation_networks.inspection_tool.current_mode";
    public static final String CURRENT_SUBMODE_KEY = "item.circulation_networks.inspection_tool.current_submode";
    public static final String SWITCH_MODE_USAGE_KEY = "item.circulation_networks.inspection_tool.usage.switch_mode";
    public static final String SWITCH_SUBMODE_USAGE_KEY = "item.circulation_networks.inspection_tool.usage.switch_submode";

    private final ToolFunction function;
    private final int subMode;

    public InspectionToolSelection(ToolFunction function, int subMode) {
        this.function = function;
        this.subMode = subMode;
    }

    public static InspectionToolSelection fromStack(ItemStack stack) {
        return new InspectionToolSelection(InspectionToolState.getFunction(stack), InspectionToolState.getSubMode(stack));
    }

    public ToolFunction function() {
        return function;
    }

    public int subMode() {
        return subMode;
    }

    public String modeLangKey() {
        return function.getLangKey();
    }

    public String modeDisplayKey() {
        return MODE_DISPLAY_KEY;
    }

    public String currentModeDisplayKey() {
        return CURRENT_MODE_KEY;
    }

    public String currentSubModeDisplayKey() {
        return CURRENT_SUBMODE_KEY;
    }

    public String subModeLangKey() {
        return function.getSubModeLangKey(subMode);
    }

    public String descriptionLangKey() {
        return function.getDescriptionLangKey();
    }

    public String switchModeUsageKey() {
        return SWITCH_MODE_USAGE_KEY;
    }

    public String switchSubModeUsageKey() {
        return SWITCH_SUBMODE_USAGE_KEY;
    }

    public TooltipModel tooltipModel() {
        return TooltipModel.fromSelection(this);
    }

    public static final class TooltipModel {

        private final java.util.List<Line> lines;

        private TooltipModel(java.util.List<Line> lines) {
            this.lines = lines;
        }

        private static TooltipModel fromSelection(InspectionToolSelection selection) {
            java.util.List<Line> lines = new ObjectArrayList<>();
            lines.add(Line.translatableWithTranslatedArg(selection.currentModeDisplayKey(), selection.modeLangKey()));
            lines.add(Line.translatableWithTranslatedArg(selection.currentSubModeDisplayKey(), selection.subModeLangKey()));
            lines.add(Line.description(selection.descriptionLangKey()));
            lines.add(Line.blank());
            lines.add(Line.translatable(selection.switchModeUsageKey()));
            lines.add(Line.translatable(selection.switchSubModeUsageKey()));
            return new TooltipModel(lines);
        }

        public java.util.List<Line> lines() {
            return lines;
        }
    }

    public static final class Line {

        private final Kind kind;
        private final String key;
        private final String argumentKey;

        private Line(Kind kind, String key, String argumentKey) {
            this.kind = kind;
            this.key = key;
            this.argumentKey = argumentKey;
        }

        public static Line translatable(String key) {
            return new Line(Kind.TRANSLATABLE, key, null);
        }

        public static Line translatableWithTranslatedArg(String key, String argumentKey) {
            return new Line(Kind.TRANSLATABLE_WITH_TRANSLATED_ARG, key, argumentKey);
        }

        public static Line description(String key) {
            return new Line(Kind.DESCRIPTION, key, null);
        }

        public static Line blank() {
            return new Line(Kind.BLANK, null, null);
        }

        public Kind kind() {
            return kind;
        }

        public String key() {
            return key;
        }

        public String argumentKey() {
            return argumentKey;
        }
    }

    public enum Kind {
        TRANSLATABLE,
        TRANSLATABLE_WITH_TRANSLATED_ARG,
        DESCRIPTION,
        BLANK
    }
}