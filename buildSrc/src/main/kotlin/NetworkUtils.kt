import java.net.Inet4Address
import java.net.NetworkInterface

fun getLocalIPv4(): List<String> {
    val ip4s = mutableListOf<String>()
    NetworkInterface.getNetworkInterfaces().asSequence()
        .filter { it.isUp && !it.isLoopback && !it.isVirtual }
        .forEach { networkInterface ->
            networkInterface.inetAddresses.asSequence()
                .filter { it is Inet4Address && !it.isLoopbackAddress }
                .forEach { ip4s.add(it.hostAddress) }
        }
    return ip4s
}