package de.codemakers.bot.supreme.util;

import de.codemakers.bot.supreme.util.updater.Updateable;
import de.codemakers.bot.supreme.util.updater.Updater;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * NetworkUtil
 *
 * @author Panzer1119
 */
public class NetworkUtil {

    public static final Pattern IP_PATTERN = Pattern.compile("(?:\\d{1,3}\\.){3}(?:\\d{1,3})");

    private static String IP_ADDRESS = null;

    static {
        Updater.addUpdateable(new Updateable() {
            @Override
            public long update(long timestamp) {
                if (!loadIPAddress()) {
                    return 10_000;
                }
                return 1_800_000;
            }

            @Override
            public void delete() {
            }
        });
    }

    public static final void init() {
    }

    public static final ArrayList<InetAddress> getInetAddresses() {
        return getInetAddresses(InetAddressFilter.ALL_FILTER);
    }

    public static final ArrayList<InetAddress> getInetAddresses(InetAddressFilter inetAddressFilter) {
        try {
            final ArrayList<InetAddress> inetAddresses = new ArrayList<>();
            final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                final NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (networkInterface == null || networkInterface.isLoopback() || !networkInterface.isUp() || networkInterface.isVirtual() || networkInterface.isPointToPoint()) {
                    continue;
                }
                final Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    final InetAddress inetAddress = addresses.nextElement();
                    if (inetAddress == null) {
                        continue;
                    }
                    if (inetAddressFilter.filter(inetAddress)) {
                        inetAddresses.add(inetAddress);
                    }
                }
            }
            return inetAddresses;
        } catch (Exception ex) {
            System.err.println("NetworkUtil: getInetAddresses error");
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static interface InetAddressFilter {

        public static final InetAddressFilter IPv4_FILTER = (inetAddress) -> Inet4Address.class.equals(inetAddress.getClass());
        public static final InetAddressFilter IPv6_FILTER = (inetAddress) -> Inet6Address.class.equals(inetAddress.getClass());
        public static final InetAddressFilter ALL_FILTER = (inetAddress) -> true;
        public static final InetAddressFilter NOTHING_FILTER = (inetAddress) -> false;

        boolean filter(InetAddress inetAddress);

    }

    private static final boolean loadIPAddress() {
        IP_ADDRESS = readIPAddress();
        return IP_ADDRESS != null && IP_PATTERN.matcher(IP_ADDRESS).matches();
    }

    private static final String readIPAddress() {
        try {
            final URL url = new URL("http://checkip.amazonaws.com/");
            String ip = null;
            int i = 0;
            while (i < 10 && (ip == null || !IP_PATTERN.matcher(ip).matches())) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    ip = br.readLine();
                    if (i > 0) {
                        Thread.sleep(1000);
                    }
                }
                i++;
            }
            return ip;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("NetworkUtil: getIPAddress error");
            return null;
        }
    }

    public static final String getIPAddress() {
        return IP_ADDRESS;
    }

}
