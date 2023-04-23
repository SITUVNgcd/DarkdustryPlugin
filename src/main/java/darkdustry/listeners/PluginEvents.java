package darkdustry.listeners;

import arc.Events;
import arc.util.Log;
import arc.util.Timer;
import darkdustry.components.Cache;
import darkdustry.components.Config.Gamemode;
import darkdustry.discord.Bot;
import darkdustry.features.Alerts;
import darkdustry.features.Ranks;
import darkdustry.features.history.*;
import darkdustry.features.menus.MenuHandler;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import mindustry.content.Blocks;
import mindustry.content.UnitTypes;
import mindustry.entities.Units;
import mindustry.game.EventType.*;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import useful.Bundle;

import static arc.Core.app;
import static darkdustry.PluginVars.*;
import static darkdustry.components.Database.getPlayerData;
import static darkdustry.components.Database.savePlayerData;
import static darkdustry.discord.Bot.botChannel;
import static darkdustry.discord.Bot.sendMessage;
import static mindustry.Vars.state;
import static mindustry.net.Administration.Config.serverName;

public class PluginEvents {

    public static void load() {
        Events.on(ServerLoadEvent.class, event -> sendMessage(botChannel, EmbedCreateSpec.builder()
                .color(Color.SUMMER_SKY)
                .title("Server launched")
                .build()));

        Events.on(PlayEvent.class, event -> {
            state.rules.unitPayloadUpdate = true;
            state.rules.showSpawns = true;

            state.rules.revealedBlocks.addAll(Blocks.slagCentrifuge, Blocks.heatReactor, Blocks.scrapWall, Blocks.scrapWallLarge, Blocks.scrapWallHuge, Blocks.scrapWallGigantic, Blocks.thruster);

            if (state.rules.infiniteResources)
                state.rules.revealedBlocks.addAll(Blocks.shieldProjector, Blocks.largeShieldProjector, Blocks.beamLink);
        });

        Events.on(GameOverEvent.class, event -> Groups.player.each(player -> {
            var data = Cache.get(player);
            data.gamesPlayed++;

            if (player.team().isEnemy(event.winner)) return;

            if (config.mode == Gamemode.attack)
                data.attackWins++;

            if (config.mode == Gamemode.pvp || config.mode == Gamemode.castle)
                data.pvpWins++;

            if (config.mode == Gamemode.hexed)
                data.hexedWins++;
        }));

        Events.on(WaveEvent.class, event -> Groups.player.each(player -> Cache.get(player).wavesSurvived++));

        Events.on(WorldLoadEvent.class, event -> {
            History.clear();
            app.post(Bot::updateActivity);
        });

        Events.on(WithdrawEvent.class, event -> {
            if (History.enabled() && event.player != null)
                History.put(new WithdrawEntry(event), event.tile.tile);
        });

        Events.on(DepositEvent.class, event -> {
            if (History.enabled() && event.player != null)
                History.put(new DepositEntry(event), event.tile.tile);

            Alerts.depositAlert(event);
        });

        Events.on(ConfigEvent.class, event -> {
            if (History.enabled() && event.player != null)
                History.put(new ConfigEntry(event), event.tile.tile);
        });

        Events.on(TapEvent.class, event -> {
            if (!History.enabled() || !Cache.get(event.player).history) return;

            var stack = History.get(event.tile.array());
            if (stack == null) return;

            var builder = new StringBuilder();

            if (stack.isEmpty()) builder.append(Bundle.get("history.empty", event.player));
            else stack.each(entry -> builder.append("\n").append(entry.getMessage(event.player)));

            Bundle.send(event.player, "history.title", event.tile.x, event.tile.y, builder.toString());
        });

        Events.on(BlockBuildEndEvent.class, event -> {
            if (!event.unit.isPlayer()) return;

            if (History.enabled() && event.tile.build != null)
                History.put(new BlockEntry(event), event.tile);

            if (event.breaking) brokenBlocksCache.increment(event.unit.getPlayer().id);
            else placedBlocksCache.increment(event.unit.getPlayer().id);
        });

        Events.on(BuildSelectEvent.class, event -> {
            if (event.breaking || event.builder == null || event.builder.buildPlan() == null || !event.builder.isPlayer())
                return;

            Alerts.buildAlert(event);
        });

        Events.on(GeneratorPressureExplodeEvent.class, event -> app.post(() -> {
            if (!Units.canCreate(event.build.team, UnitTypes.renale)) return;

            Call.spawnEffect(event.build.x, event.build.y, 0f, UnitTypes.renale);
            UnitTypes.renale.spawn(event.build.team, event.build);
        }));

        Events.on(PlayerJoin.class, event -> {
            var data = getPlayerData(event.player);
            Cache.put(event.player, data);
            Ranks.name(event.player, data);

            // Вызываем с задержкой, чтобы игрок успел появиться
            app.post(() -> data.effects.join.get(event.player));

            Log.info("@ has connected. [@]", event.player.plainName(), event.player.uuid());
            Bundle.send("events.join", event.player.coloredName());
            Bundle.send(event.player, "welcome.message", serverName.string(), discordServerUrl);

            sendMessage(botChannel, EmbedCreateSpec.builder()
                    .color(Color.MEDIUM_SEA_GREEN)
                    .title(event.player.plainName() + " joined")
                    .build());

            if (data.welcomeMessage)
                MenuHandler.showWelcomeMenu(event.player);

            app.post(Bot::updateActivity);
        });

        Events.on(PlayerLeave.class, event -> {
            var data = Cache.remove(event.player);
            savePlayerData(data);

            data.effects.leave.get(event.player);

            Log.info("@ has disconnected. [@]", event.player.plainName(), event.player.uuid());
            Bundle.send("events.leave", event.player.coloredName());

            sendMessage(botChannel, EmbedCreateSpec.builder()
                    .color(Color.CINNABAR)
                    .title(event.player.plainName() + " left")
                    .build());

            if (vote != null) vote.left(event.player);
            if (voteKick != null) voteKick.left(event.player);

            app.post(Bot::updateActivity);
        });

        Timer.schedule(() -> Groups.player.each(player -> {
            if (player.unit().moving())
                Cache.get(player).effects.move.get(player);
        }), 0f, 0.1f);
    }
}