package com.example.ui.lab

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PracticeLabViewModel : ViewModel() {

    // --- Core Password Lab State (From Prompt) ---
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _analysis = MutableStateFlow(
        PasswordAnalysis(
            score = 0,
            label = "Enter a lab password",
            suggestions = emptyList()
        )
    )
    val analysis: StateFlow<PasswordAnalysis> = _analysis.asStateFlow()

    private val _hashDemo = MutableStateFlow<HashDemo?>(null)
    val hashDemo: StateFlow<HashDemo?> = _hashDemo.asStateFlow()

    private val _simulationResult = MutableStateFlow("")
    val simulationResult: StateFlow<String> = _simulationResult.asStateFlow()

    // --- Module Navigation / Filtering State ---
    val availableModules = listOf(
        LabModule("all", "All Labs", "Complete cybersecurity learning suite", "🧪"),
        LabModule("password", "Password Lab", "Strength & cryptographic hashing", "🔐"),
        LabModule("network", "Network Lab", "Subnets, ports & protocols", "🌐"),
        LabModule("android", "Android Security", "Manifest & APK configuration", "📱"),
        LabModule("vulnerability", "Vulnerability Lab", "Hands-on flaw remediation", "🛡️"),
        LabModule("report", "Lab Report", "Bug tracking & audit findings", "📊")
    )

    private val _selectedModuleId = MutableStateFlow("all")
    val selectedModuleId: StateFlow<String> = _selectedModuleId.asStateFlow()

    // --- Network Lab State ---
    private val _networkIp = MutableStateFlow("192.168.1.10")
    val networkIp: StateFlow<String> = _networkIp.asStateFlow()

    private val _networkCidr = MutableStateFlow(24)
    val networkCidr: StateFlow<Int> = _networkCidr.asStateFlow()

    private val _subnetResult = MutableStateFlow<SubnetCalculation?>(
        NetworkLab.calculateSubnet("192.168.1.10", 24)
    )
    val subnetResult: StateFlow<SubnetCalculation?> = _subnetResult.asStateFlow()

    private val _portQuery = MutableStateFlow("")
    val portQuery: StateFlow<String> = _portQuery.asStateFlow()

    // --- Android Security Audit State ---
    private val _auditItems = MutableStateFlow<List<AndroidSecurityAuditItem>>(emptyList())
    val auditItems: StateFlow<List<AndroidSecurityAuditItem>> = _auditItems.asStateFlow()

    // --- Vulnerability Lab State ---
    private val _selectedVulnerability = MutableStateFlow(VulnerabilityLab.topics.first())
    val selectedVulnerability: StateFlow<VulnerabilityTopic> = _selectedVulnerability.asStateFlow()

    private val _sqliInput = MutableStateFlow("admin' OR '1'='1")
    val sqliInput: StateFlow<String> = _sqliInput.asStateFlow()

    private val _sqliResult = MutableStateFlow(
        VulnerabilityLab.testSqlInjectionSimulation("admin' OR '1'='1")
    )
    val sqliResult: StateFlow<String> = _sqliResult.asStateFlow()

    // --- Lab Report Findings State ---
    private val _findings = MutableStateFlow(
        listOf(
            LabFinding(
                id = "finding-1",
                title = "Weak Default Lab Password Detected",
                severity = "High",
                target = "Authentication Module",
                risk = "Common dictionary passwords like '123456' or 'admin' can be cracked in milliseconds via automated wordlist guessing.",
                evidence = "PasswordLab dictionary match: 'password' is among top 10 commonly leaked rockyou passwords.",
                fix = "Enforce minimum 12 characters, require mixed case, digits, special characters, and reject leaked dictionary passwords.",
                verified = true
            ),
            LabFinding(
                id = "finding-2",
                title = "Unsalted Hashing Allows Rainbow Table Lookup",
                severity = "Medium",
                target = "Credential Storage",
                risk = "Identical passwords generate identical hash digests, enabling adversaries to reverse hashes using precomputed lookup tables.",
                evidence = "SHA-256 without salt produces deterministic digest across all user accounts.",
                fix = "Implement cryptographically secure 16-byte random salts per credential and use Argon2id or PBKDF2 with >=210,000 iterations.",
                verified = true
            ),
            LabFinding(
                id = "finding-3",
                title = "Unparameterized Query Injection Vulnerability",
                severity = "Critical",
                target = "Local Database",
                risk = "Direct string concatenation allows an attacker to inject SQL operators and bypass access controls.",
                evidence = "Input ' OR '1'='1 resulted in unauthorized record disclosure in simulated test.",
                fix = "Use parameterized SQL queries with PreparedStatement or Room DAO binding.",
                verified = false
            )
        )
    )
    val findings: StateFlow<List<LabFinding>> = _findings.asStateFlow()

    // --- Core Password Lab Implementation ---
    fun updatePassword(value: String) {
        _password.value = value
        _analysis.value = PasswordLab.analyze(value)
        _simulationResult.value = ""
    }

    fun generateHashDemo() {
        val value = _password.value

        if (value.isEmpty()) {
            _hashDemo.value = null
            return
        }

        val salt = PasswordLab.createSalt()
        val hash = PasswordLab.sha256(value, salt)
        val md5 = PasswordLab.md5(value)

        _hashDemo.value = HashDemo(
            password = value,
            salt = PasswordLab.bytesToHex(salt),
            hash = hash,
            md5Hash = md5
        )
    }

    fun runSafeGuessingSimulation() {
        val target = _password.value

        if (target.isEmpty()) {
            _simulationResult.value =
                "Enter a dummy lab password first."
            return
        }

        val demoWords = listOf(
            "123456",
            "password",
            "12345678",
            "admin",
            "qwerty",
            "7first",
            "welcome",
            "letmein",
            "iloveyou",
            "monkey"
        )

        val position = demoWords.indexOfFirst {
            it.equals(target, ignoreCase = true)
        }

        _simulationResult.value =
            if (position >= 0) {
                "⚠️ Lab simulation: '$target' appeared in the demo word list at attempt ${position + 1}. No real account or device was accessed. Cracking time: < 0.001 seconds."
            } else {
                "🛡️ Lab simulation: '$target' was not found in the small demo dictionary list (${demoWords.size} words). Note: Full attacker dictionaries contain billions of candidate passwords."
            }
    }

    fun fillSamplePassword() {
        val sample = PasswordLab.generateDummyPassword()
        updatePassword(sample)
    }

    fun clear() {
        _password.value = ""
        _analysis.value = PasswordLab.analyze("")
        _hashDemo.value = null
        _simulationResult.value = ""
    }

    // --- Module Navigation ---
    fun selectModule(id: String) {
        _selectedModuleId.value = id
    }

    // --- Network Lab Controls ---
    fun updateNetworkIp(ip: String) {
        _networkIp.value = ip
        _subnetResult.value = NetworkLab.calculateSubnet(ip, _networkCidr.value)
    }

    fun updateNetworkCidr(cidr: Int) {
        _networkCidr.value = cidr
        _subnetResult.value = NetworkLab.calculateSubnet(_networkIp.value, cidr)
    }

    fun updatePortQuery(query: String) {
        _portQuery.value = query
    }

    // --- Android Security Audit ---
    fun runAndroidAudit(context: Context) {
        _auditItems.value = AndroidSecurityLab.performAudit(context)
    }

    // --- Vulnerability Lab Controls ---
    fun selectVulnerabilityTopic(topic: VulnerabilityTopic) {
        _selectedVulnerability.value = topic
    }

    fun updateSqliInput(input: String) {
        _sqliInput.value = input
        _sqliResult.value = VulnerabilityLab.testSqlInjectionSimulation(input)
    }

    // --- Lab Report Controls ---
    fun toggleFindingVerified(findingId: String) {
        _findings.value = _findings.value.map {
            if (it.id == findingId) it.copy(verified = !it.verified) else it
        }
    }

    fun addFinding(title: String, severity: String, risk: String, evidence: String, fix: String) {
        val newFinding = LabFinding(
            id = "finding-${System.currentTimeMillis()}",
            title = title.ifBlank { "Untitled Finding" },
            severity = severity,
            target = "Practice Lab Audit",
            risk = risk.ifBlank { "Unassessed security risk." },
            evidence = evidence.ifBlank { "Observed during lab testing." },
            fix = fix.ifBlank { "Apply security hardening best practices." },
            verified = false
        )
        _findings.value = listOf(newFinding) + _findings.value
    }

    fun deleteFinding(findingId: String) {
        _findings.value = _findings.value.filterNot { it.id == findingId }
    }

    fun generateMarkdownReport(): String {
        val sb = StringBuilder()
        sb.append("# 🧪 Practice Lab Security Audit Report\n\n")
        sb.append("**Generated:** ").append(java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(java.util.Date())).append("\n")
        sb.append("**Environment:** Local Test & Educational Environment\n\n")
        sb.append("## Findings Summary\n\n")
        _findings.value.forEachIndexed { index, item ->
            sb.append("### ${index + 1}. ${item.title} [${item.severity.uppercase()}]\n")
            sb.append("- **Verification Status:** ").append(if (item.verified) "✅ Verified Remediation" else "⏳ Pending Verification").append("\n")
            sb.append("- **Risk Assessment:** ${item.risk}\n")
            sb.append("- **Evidence:** ${item.evidence}\n")
            sb.append("- **Remediation / Fix:** ${item.fix}\n\n")
        }
        return sb.toString()
    }
}
