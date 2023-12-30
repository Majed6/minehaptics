package com.example.examplemod;


import com.bhaptics.haptic.HapticPlayerImpl;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Objects;


public class MyForgeEventHandler {
    private final HapticPlayerImpl hapticPlayer;
    private float previousFoodLevel = 20;

    private boolean isSubmerged = false;

    private static class Tact {
        public String tactFile;
        public boolean isRegistered;

        public Tact(String tactFile) {
            this.tactFile = tactFile;
            this.isRegistered = false;
        }
    }

    private HashMap<String, Tact> Tacts = new HashMap<String, Tact>() {{
        put("HUNGER", new Tact("hunger_0"));
        put("LOW_HEALTH", new Tact("low_health_0"));
        put("CONSUME", new Tact("consume_0"));
        put("CONSUME_EFFECT", new Tact("consume_effect_0"));
        put("RAIN", new Tact("rain_0"));
        put("RAIN_TACVISOR", new Tact("rain_0_tacvisor"));
        put("SNOW_TACVISOR", new Tact("snow_0_tacvisor"));
        put("SWING_LEFT", new Tact("swing_0_left_tactosy"));
        put("SWING_RIGHT", new Tact("swing_0_right_tactosy"));
        put("SLASH_LEFT", new Tact("slash_8_left_tactosy"));
        put("SLASH_RIGHT", new Tact("slash_8_right_tactosy"));
        put("PICKUP", new Tact("backpack_0"));
        put("HIT", new Tact("hit_0"));
        put("SUBMERGED", new Tact("holding_breath_0_tacvisor"));
    }};


    public MyForgeEventHandler() {
        // create a haptic player
        String appId = "com.bhaptics.sample";
        String appName = "Sample Java App";
        this.hapticPlayer =
                new HapticPlayerImpl(appId, appName, true);
    }

    private void submitTact(Tact tact) {
        if (!tact.isRegistered) {
            this.hapticPlayer.register(tact.tactFile, Utils.getTactString(tact.tactFile));
            tact.isRegistered = true;
        }
        this.hapticPlayer.submitRegistered(tact.tactFile);
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        // Damaged
        if (event.getEntity() instanceof Player player) {
            this.submitTact(Tacts.get("HIT"));
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        //Run on every two seconds only
        if (event.player.tickCount % 40 != 0) {
            return;
        }

        Player player = event.player;
        // Hunger
        int foodLevel = player.getFoodData().getFoodLevel();
        if (foodLevel <= 10 && foodLevel != this.previousFoodLevel) {
            this.previousFoodLevel = foodLevel;
            this.submitTact(Tacts.get("HUNGER"));
        }
        // Low health
        if (player.getHealth() <= 5) {
            this.submitTact(Tacts.get("LOW_HEALTH"));
        }

        if (!player.isInWater()) {
            // Snow
            if (Utils.isSnowingOnPlayer(player)) {
                this.submitTact(Tacts.get("SNOW_TACVISOR"));
            }
            // Rain
            else if (player.isInWaterOrRain()) {
                this.submitTact(Tacts.get("RAIN_TACVISOR"));
                this.submitTact(Tacts.get("RAIN"));
            }

        }


        // Submerged for the first time
        boolean underWater = player.isUnderWater(); // in water or not
        if (this.isSubmerged != underWater) { // if changed
            this.isSubmerged = underWater; // update
            if (this.isSubmerged) { // if submerged for the first time
                this.submitTact(Tacts.get("SUBMERGED"));
            }
        }

    }

    @SubscribeEvent
    public void onItemUse(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getItem().isEdible()) {
                if (Objects.requireNonNull(event.getItem().getFoodProperties(player)).getEffects().isEmpty()) {
                    //food
                    this.submitTact(Tacts.get("CONSUME"));
                } else {
                    //super food
                    this.submitTact(Tacts.get("CONSUME_EFFECT"));
                }
            }

        }
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        // Attack
        this.submitTact(Tacts.get("SLASH_RIGHT"));
    }

    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        // Swing
        this.submitTact(Tacts.get("SWING_RIGHT"));
    }


    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        // Swing
        this.submitTact(Tacts.get("SWING_LEFT"));
    }

    @SubscribeEvent
    public void pickupItem(EntityItemPickupEvent event) {
        this.submitTact(Tacts.get("PICKUP"));
    }
}
