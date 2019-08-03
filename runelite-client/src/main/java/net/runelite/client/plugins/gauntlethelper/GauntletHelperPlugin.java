package net.runelite.client.plugins.gauntlethelper;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import javax.inject.Inject;

import static net.runelite.api.AnimationID.IDLE;

@PluginDescriptor(
        name = "Gauntlet Helper",
        description = "Provides various overlays to assist with the gauntlet minigame",
        tags = {"overlay", "bosses"},
        enabledByDefault = false
)

public class GauntletHelperPlugin extends Plugin {
    @Inject
    private OverlayManager overlayManager;

    @Inject
    private GauntletHelperOverlay overlay;

    @Inject
    private Client client;

    public boolean is_boss_using_range = true;
    private int num_boss_hits = 0;

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(overlay);
        resetPlugin();
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(overlay);
        resetPlugin();
    }

    @Provides
    GauntletHelperConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(GauntletHelperConfig.class);
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged animationChanged){
        if (animationChanged.getActor().getAnimation() == IDLE){
            return;
        }
        if (animationChanged.getActor() == client.getLocalPlayer()){
            return;
        }
        if (animationChanged.getActor().getName() != null){
            if (animationChanged.getActor().getName().contains("Hunllef")){
                switch(animationChanged.getActor().getAnimation()){
                    case 8419:
                    case 8418:
                        num_boss_hits++;
                        if (num_boss_hits == 4) {
                            num_boss_hits = 0;
                            is_boss_using_range = !is_boss_using_range;
                        }
                        break;
                    default:
                        break;
                }
                //System.out.println("Hunllef animation: " + animationChanged.getActor().getAnimation());
                //System.out.println("Num_boss_hits: " + num_boss_hits);
            }
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged){
        if (gameStateChanged.getGameState() == GameState.LOADING){
            resetPlugin();
        }
    }

    private void resetPlugin(){
        num_boss_hits = 0;
        is_boss_using_range = true;
    }

}
