package darkdustry;

import arc.struct.IntIntMap;
import arc.struct.Seq;
import arc.util.CommandHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import darkdustry.components.Config;
import darkdustry.features.votes.VoteKick;
import darkdustry.features.votes.VoteSession;
import mindustry.core.Version;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_DASHES;
import static mindustry.Vars.tilesize;

public class PluginVars {

    /**
     * Необходимое количество игроков для успешного завершения голосования.
     */
    public static final float voteRatio = 0.55f;

    /**
     * Время, после которого голосование автоматически завершится. В секундах.
     */
    public static final int voteDuration = 50;

    /**
     * Стандартная длительность кика игрока. В миллисекундах.
     */
    public static final long kickDuration = 30 * 60 * 1000L;

    /**
     * Время, после которого будет загружена карта. В секундах.
     */
    public static final int mapLoadDelay = 10;

    /**
     * Расстояние до ядра, в котором отслеживаются опасные блоки. Интервал оповещений об опасных блоках.
     */
    public static final int alertsDistance = 16 * tilesize, alertsTimer = 3;

    /**
     * Максимальное количество пропущенных волн.
     */
    public static final int maxVnwAmount = 10;

    /**
     * Максимальное количество выдаваемых ресурсов.
     */
    public static final int maxGiveAmount = 100000;

    /**
     * Максимальное количество создаваемых юнитов.
     */
    public static final int maxSpawnAmount = 25;

    /**
     * Максимальная длительность применяемого эффекта статуса. В секундах.
     */
    public static final int maxEffectDuration = 300;

    /**
     * Максимальная площадь для заливки.
     */
    public static final int maxFillArea = 512;

    /**
     * Количество команд/игроков/карт/сохранений на одной странице списка.
     */
    public static final int maxPerPage = 6;

    /**
     * Максимальное количество записей истории на один тайл.
     */
    public static final int maxHistoryCapacity = 6;

    /**
     * Максимально допустимое количество игроков с одинаковыми IP адресами.
     */
    public static final int maxIdenticalIPs = 3;

    /**
     * Версия Mindustry, запущенная на сервере.
     */
    public static final int mindustryVersion = Version.build;

    /**
     * Язык по умолчанию.
     */
    public static final String defaultLanguage = "en";

    /**
     * Название файла, в котором хранится конфигурация сервера.
     */
    public static final String configFileName = "config.json";

    /**
     * Ссылка на наш Discord сервер
     */
    public static final String discordServerUrl = "https://discord.gg/uDPPVN6V3E";

    /**
     * Ссылка на translation API.
     */
    public static final String translationApiUrl = "https://clients5.google.com/translate_a/t?client=dict-chrome-ex&dt=t";

    /**
     * Список команд, доступных только администраторам игрового сервера. Список скрытых команд, которые не отображаются в /help. Список команд, которые показываются в приветственном сообщении.
     */
    public static final Seq<String> adminOnlyCommands = new Seq<>(), hiddenCommands = Seq.with("login"), welcomeMessageCommands = Seq.with("help", "settings", "stats");

    /**
     * Кэш количества построенных и разрушенных блоков.
     */
    public static final IntIntMap placedBlocksCache = new IntIntMap(), brokenBlocksCache = new IntIntMap();

    /**
     * Используется для считывания и записи Json объектов.
     */
    public static final Gson gson = new GsonBuilder().setFieldNamingPolicy(LOWER_CASE_WITH_DASHES).setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();

    /**
     * Используются для форматирования времени в дату.
     */
    public static final DateTimeFormatter shortDateFormat = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.of("Europe/Moscow")),
            longDateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").withZone(ZoneId.of("Europe/Moscow"));

    /**
     * Конфигурация сервера.
     */
    public static Config config;

    /**
     * Текущее голосование.
     */
    public static VoteSession vote;

    /**
     * Текущее голосование за кик игрока.
     */
    public static VoteKick voteKick;

    /**
     * Кэшированные хандлеры, которые использовались для регистрации команд.
     */
    public static CommandHandler clientCommands, serverCommands, discordCommands;
}