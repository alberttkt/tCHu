package ch.epfl.tchu.net;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.stream.Collectors;

/**
 * @author Albert Troussard (330361)
 * @author Menelik Nouvellon (328132)
 */
public final class ShowMyIpAddress {
    public static void main(String[] args) throws IOException {
        System.out.println(ip());
    }

    public static String ip() throws SocketException {
        return NetworkInterface.networkInterfaces()
                .filter(i -> {
                    try { return i.isUp() && !i.isLoopback(); }
                    catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                })
                .flatMap(NetworkInterface::inetAddresses)
                .filter(a -> a instanceof Inet4Address)
                .map(InetAddress::getCanonicalHostName).collect(Collectors.joining());
    }
}
