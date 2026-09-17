package com.example.ui.lab

object NetworkLab {

    val commonSecurityPorts = listOf(
        PortInfo(
            port = 21,
            service = "FTP",
            protocol = "TCP",
            description = "File Transfer Protocol",
            securityImplication = "Transmits credentials and data in unencrypted plaintext. Susceptible to sniffing and MITM attacks.",
            recommendedFix = "Migrate to SFTP (SSH File Transfer Protocol on port 22) or FTPS (FTP over TLS)."
        ),
        PortInfo(
            port = 22,
            service = "SSH",
            protocol = "TCP",
            description = "Secure Shell",
            securityImplication = "Encrypted remote management. Target of aggressive brute-force dictionary attacks.",
            recommendedFix = "Disable password auth, mandate Ed25519 public keys, disable root login, deploy Fail2ban."
        ),
        PortInfo(
            port = 23,
            service = "Telnet",
            protocol = "TCP",
            description = "Legacy Remote Terminal",
            securityImplication = "Critical flaw: entirely unencrypted plaintext traffic. Highly obsolete and insecure.",
            recommendedFix = "Block port 23 completely and replace with SSH."
        ),
        PortInfo(
            port = 53,
            service = "DNS",
            protocol = "UDP/TCP",
            description = "Domain Name System",
            securityImplication = "DNS spoofing / cache poisoning can redirect users to rogue phishing servers. Amplification DDoS vectors.",
            recommendedFix = "Deploy DNSSEC, DNS over HTTPS (DoH), or DNS over TLS (DoT)."
        ),
        PortInfo(
            port = 80,
            service = "HTTP",
            protocol = "TCP",
            description = "HyperText Transfer Protocol",
            securityImplication = "Unencrypted web traffic. Susceptible to cookie theft, session hijacking, and content tampering.",
            recommendedFix = "Redirect all HTTP traffic with 301 Permanent Redirect to HTTPS (Port 443) and enforce HSTS."
        ),
        PortInfo(
            port = 443,
            service = "HTTPS",
            protocol = "TCP",
            description = "HTTP over TLS/SSL",
            securityImplication = "Secures web traffic with encryption and server identity verification.",
            recommendedFix = "Enforce TLS 1.3, configure modern cipher suites, and pin certificates in mobile apps where appropriate."
        ),
        PortInfo(
            port = 3306,
            service = "MySQL / MariaDB",
            protocol = "TCP",
            description = "Relational Database Port",
            securityImplication = "Exposing database ports to public internet leads to unauthorized access and automated ransomware sweeps.",
            recommendedFix = "Bind database to 127.0.0.1 loopback only. Use SSH bastion tunnels or private VPCs."
        ),
        PortInfo(
            port = 3389,
            service = "RDP",
            protocol = "TCP/UDP",
            description = "Remote Desktop Protocol",
            securityImplication = "Primary target for enterprise ransomware gangs via brute force and known CVE exploits (e.g. BlueKeep).",
            recommendedFix = "Never expose 3389 directly. Require WireGuard/IPsec VPN with Multi-Factor Authentication (MFA)."
        ),
        PortInfo(
            port = 8080,
            service = "HTTP-Alt / Proxy",
            protocol = "TCP",
            description = "Alternate Web / Proxy Port",
            securityImplication = "Frequently used for dev servers, testing backends, or burp proxies with weaker security controls.",
            recommendedFix = "Audit non-standard ports; ensure development instances are never exposed on production networks."
        )
    )

    fun calculateSubnet(ipStr: String, cidr: Int): SubnetCalculation? {
        try {
            val parts = ipStr.trim().split(".")
            if (parts.size != 4) return null
            val octets = parts.map { it.toIntOrNull() ?: return null }
            if (octets.any { it !in 0..255 }) return null
            if (cidr !in 1..32) return null

            val ipNum = (octets[0].toLong() shl 24) or
                    (octets[1].toLong() shl 16) or
                    (octets[2].toLong() shl 8) or
                    octets[3].toLong()

            val maskNum = if (cidr == 32) 0xFFFFFFFFL else ((0xFFFFFFFFL shl (32 - cidr)) and 0xFFFFFFFFL)
            val networkNum = ipNum and maskNum
            val broadcastNum = networkNum or (maskNum.inv() and 0xFFFFFFFFL)

            val totalHosts = if (cidr >= 31) 2L else (1L shl (32 - cidr)) - 2L
            val firstHostNum = if (cidr >= 31) networkNum else networkNum + 1
            val lastHostNum = if (cidr >= 31) broadcastNum else broadcastNum - 1

            val isPrivate = (octets[0] == 10) ||
                    (octets[0] == 172 && octets[1] in 16..31) ||
                    (octets[0] == 192 && octets[1] == 168) ||
                    (octets[0] == 127)

            return SubnetCalculation(
                ip = ipStr.trim(),
                cidr = cidr,
                netmask = formatIp(maskNum),
                networkAddress = formatIp(networkNum),
                broadcastAddress = formatIp(broadcastNum),
                firstUsableHost = formatIp(firstHostNum),
                lastUsableHost = formatIp(lastHostNum),
                totalHosts = maxOf(0L, totalHosts),
                isPrivateIp = isPrivate
            )
        } catch (e: Exception) {
            return null
        }
    }

    private fun formatIp(ip: Long): String {
        val o1 = (ip shr 24) and 0xFF
        val o2 = (ip shr 16) and 0xFF
        val o3 = (ip shr 8) and 0xFF
        val o4 = ip and 0xFF
        return "$o1.$o2.$o3.$o4"
    }
}
