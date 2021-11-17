package pandorum.discord;

import arc.struct.Seq;
import arc.util.Log;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.rest.request.RouteMatcher;
import discord4j.rest.response.ResponseFunction;
import discord4j.rest.route.Routes;
import discord4j.rest.util.AllowedMentions;
import discord4j.rest.util.Color;
import pandorum.Misc;
import pandorum.PandorumPlugin;
import pandorum.comp.Authme;

import java.util.Objects;

public class BotMain {
    public static DiscordClient bot;
    public static GatewayDiscordClient client;

    public static final Color normalColor = Color.ORANGE;
    public static final Color successColor = Color.GREEN;
    public static final Color errorColor = Color.RED;

    public static void start() {
        try {
            bot = DiscordClientBuilder.create(PandorumPlugin.config.DiscordBotToken)
                    .onClientResponse(ResponseFunction.emptyIfNotFound())
                    .onClientResponse(ResponseFunction.emptyOnErrorStatus(RouteMatcher.route(Routes.REACTION_CREATE), 400))
                    .setDefaultAllowedMentions(AllowedMentions.suppressAll()).build();

            client = bot.login().block();

            if (client == null) throw new RuntimeException("Не удалось запустить бота...");

            client.on(MessageCreateEvent.class).subscribe(event -> {
                Message msg = event.getMessage();
                if (msg.getContent().startsWith(BotHandler.prefix)) BotHandler.handler.handleMessage(msg.getContent(), msg);

                if (Objects.equals(msg.getChannel().block(), BotHandler.botChannel) && !msg.getAuthor().get().isBot() && msg.getContent().length() > 0) {
                    Misc.sendToChat("events.discord-message", Objects.requireNonNull(msg.getAuthorAsMember().block()).getDisplayName(), msg.getContent());
                    Log.info("[Discord]@: @", Objects.requireNonNull(msg.getAuthorAsMember().block()).getDisplayName(), msg.getContent());
                }
            }, e -> {});

            client.on(ButtonInteractionEvent.class).subscribe(event -> {
                Message msg = event.getMessage().get();
                String button = event.getCustomId();
                if (Authme.loginWaiting.containsKey(msg)) {
                    switch (button) {
                        case "confirm" -> {
                            BotHandler.text(msg, "**Запрос был подтвержден** " + event.getInteraction().getMember().get().getDisplayName());
                            Authme.confirm(Authme.loginWaiting.get(msg));
                        }
                        case "deny" -> {
                            BotHandler.text(msg, "**Запрос был отклонен** " + event.getInteraction().getMember().get().getDisplayName());
                            Authme.deny(Authme.loginWaiting.get(msg));
                        }
                    }
                    msg.delete().block();
                    Authme.loginWaiting.remove(msg);
                }
            }, e -> {});

            BotHandler.init();
            Log.info("[Darkdustry] Бот успешно запущен...");
        } catch(Exception e) {
            Log.err("[Darkdustry] Ошибка запуска бота...");
            System.exit(2);
        }
    }
}
