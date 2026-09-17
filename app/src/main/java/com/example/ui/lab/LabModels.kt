package com.example.ui.lab

data class LabModule(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String
)

data class PasswordAnalysis(
    val score: Int,
    val label: String,
    val suggestions: List<String>
)

data class HashDemo(
    val password: String,
    val salt: String,
    val hash: String,
    val md5Hash: String = ""
)

data class SubnetCalculation(
    val ip: String,
    val cidr: Int,
    val netmask: String,
    val networkAddress: String,
    val broadcastAddress: String,
    val firstUsableHost: String,
    val lastUsableHost: String,
    val totalHosts: Long,
    val isPrivateIp: Boolean
)

data class PortInfo(
    val port: Int,
    val service: String,
    val protocol: String,
    val description: String,
    val securityImplication: String,
    val recommendedFix: String
)

data class AndroidSecurityAuditItem(
    val checkName: String,
    val status: String,
    val isSecure: Boolean,
    val description: String,
    val recommendation: String
)

data class VulnerabilityTopic(
    val id: String,
    val title: String,
    val cweId: String,
    val severity: String,
    val vulnerabilityDescription: String,
    val vulnerableSnippet: String,
    val remediatedSnippet: String,
    val fixExplanation: String
)

data class LabFinding(
    val id: String,
    val title: String,
    val severity: String, // Low, Medium, High, Critical
    val target: String = "App Environment",
    val risk: String,
    val evidence: String,
    val fix: String,
    val verified: Boolean = false
)
