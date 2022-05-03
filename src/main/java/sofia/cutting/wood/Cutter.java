package sofia.cutting.wood;

import sofia.api.runescape.*;
import kraken.plugin.api.*;
import sofia.api.runescape.models.tree.*;

import static kraken.plugin.api.Rng.i32;

/**
 * The class for the action of the wood cutting skills.
 *
 * @author 0xfffcf
 * @version 1.0.0
 * @since 1.0.0
 */
public class Cutter extends Plugin {
    private boolean isScriptStarted;
    private boolean areLogsDropped;
    private int selectedTree;

    /* Class Functions */

    @Override
    public boolean onLoaded(PluginContext pluginContext) {
        pluginContext.setName("Wood Cutting - Sofia v1.0");
        return super.onLoaded(pluginContext);
    }

    @Override
    public int onLoop() {
        if (isScriptStarted) {
            Player localPlayer = Players.self();
            Tree tree = TreeFactory.getTreeType(selectedTree);

            if (localPlayer == null) {
                return i32(600, 800);
            }

            if (Inventory.isFull()) {
                if (areLogsDropped) {
                    SInventory.dropInventory();
                    return i32(600, 800);
                } else {
                    Area3di bankArea = tree.getBANK_AREA();

                    if (bankArea.contains(localPlayer)) {
                        SBank.depositInventoryToBank();
                        return i32(600, 800);
                    } else {
                        Move.startTraverse(bankArea.center());
                        return i32(1500, 2000);
                    }
                }
            } else {
                Area3di treeArea = tree.getOBJECT_AREA();

                if (treeArea.contains(localPlayer)) {
                    SceneObject treeObject = SObject.getObjectRecursively(tree);

                    if (treeObject == null) {
                        // TODO : World hopping
                        Move.startTraverse(treeArea.center());
                        return i32(1500, 2000);
                    } else {
                        if (!localPlayer.isAnimationPlaying()) {
                            if (treeObject.interact("Chop down")) {
                                return 1000;
                            } else {
                                return i32(600, 800);
                            }
                        }
                    }
                } else {
                    Move.startTraverse(treeArea.center());
                    return i32(1500, 2000);
                }
            }
        }
        return i32(600, 1000);
    }

    @Override
    public void onPaint() {
        ImGui.label("Sofia - Wood Cutter v1.0");
        selectedTree = ImGui.combo("Trees", new String[]{
                "Dead Tree",
                "Basic Tree",
                "Oak Tree",
                "Willow Tree",
                "Maple Tree",
                "Yew Tree",
                "Ivy",
                "Magic Tree"
        }, selectedTree);
        areLogsDropped = ImGui.checkbox("Are logs dropped: ", areLogsDropped);
        isScriptStarted = ImGui.checkbox("Start/Stop", isScriptStarted);
    }
}
