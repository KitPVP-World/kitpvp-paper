package world.kitpvp.slime.lan;

import com.mojang.logging.LogUtils;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class LanServerAnnouncement {
    public static final boolean SHOULD_SEND_ANNOUNCEMENT = "true".equals(System.getProperty("kitpvpslime.announce"));

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String PING_ADDRESS = "224.0.2.60";
    private static final int PING_PORT = 4445;
    private static final long PING_INTERVAL = 1500L;

    private final MinecraftServer server;
    private final Thread pingThread;
    private final DatagramSocket socket;

    private LanServerAnnouncement(MinecraftServer server) throws SocketException {
        this.server = server;
        this.pingThread = new Thread(this::run, "LanServerAnnouncement");
        this.pingThread.setDaemon(true);
        this.pingThread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
        this.socket = new DatagramSocket();
    }

    public void run() {
        LOGGER.info("Binding on port " + PING_PORT + " for LAN server announcements");
        String string = this.formatAnnouncement();
        byte[] bs = string.getBytes(StandardCharsets.UTF_8);
        while (!this.pingThread.isInterrupted()) {
            try {
                InetAddress inetAddress = InetAddress.getByName(PING_ADDRESS);
                DatagramPacket datagramPacket = new DatagramPacket(bs, bs.length, inetAddress, PING_PORT);
                this.socket.send(datagramPacket);
            } catch (IOException iOException) {
                LOGGER.warn("Failed to send the lan server announcement", iOException);
                break;
            }
            try {
                Thread.sleep(PING_INTERVAL);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private String formatAnnouncement() {
        return "[MOTD]" + this.server.getMotd() + "[/MOTD][AD]" + this.server.getPort() + "[/AD]";
    }

    public static void createIfEnabled(MinecraftServer server) {
        if (!SHOULD_SEND_ANNOUNCEMENT)
            return;

        try {
            new LanServerAnnouncement(server).pingThread.start();
        } catch (SocketException e) {
            LOGGER.error("Failed to start the LAN server announcer", e);
        }
    }
}
