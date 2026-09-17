package com.example.ui.lab

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn

@Composable
fun PracticeLabApp(
    viewModel: PracticeLabViewModel
) {
    com.example.ui.theme.PracticeLabAppTheme {
        PracticeLabScreen(
            viewModel = viewModel
        )
    }
}

@Composable
fun PracticeLabScreen(
    viewModel: PracticeLabViewModel
) {
    val context = LocalContext.current
    val password by viewModel.password.collectAsState()
    val analysis by viewModel.analysis.collectAsState()
    val hashDemo by viewModel.hashDemo.collectAsState()
    val simulationResult by viewModel.simulationResult.collectAsState()
    val selectedModuleId by viewModel.selectedModuleId.collectAsState()

    // Additional Lab States
    val networkIp by viewModel.networkIp.collectAsState()
    val networkCidr by viewModel.networkCidr.collectAsState()
    val subnetResult by viewModel.subnetResult.collectAsState()
    val portQuery by viewModel.portQuery.collectAsState()
    val auditItems by viewModel.auditItems.collectAsState()
    val selectedVulnerability by viewModel.selectedVulnerability.collectAsState()
    val sqliInput by viewModel.sqliInput.collectAsState()
    val sqliResult by viewModel.sqliResult.collectAsState()
    val findings by viewModel.findings.collectAsState()

    var showAddFindingDialog by remember { mutableStateOf(false) }

    // Run Android audit on launch
    LaunchedEffect(Unit) {
        viewModel.runAndroidAudit(context)
    }

    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = statusBarPadding, bottom = 24.dp)
            .testTag("practice_lab_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // --- Header Section ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🧪 Practice Lab",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Own-device / local test environment learning",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🛡️", fontSize = 22.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Module Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        viewModel.availableModules.forEach { module ->
                            val isSelected = selectedModuleId == module.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectModule(module.id) },
                                label = { Text("${module.emoji} ${module.title}") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }
            }
        }

        // --- PASSWORD LAB CARD ---
        if (selectedModuleId == "all" || selectedModuleId == "password") {
            item {
                LabCard(
                    title = "🔐 Password Lab",
                    subtitle = "Use dummy passwords only"
                ) {
                    OutlinedTextField(
                        value = password,
                        onValueChange = viewModel::updatePassword,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input"),
                        label = { Text("Dummy lab password") },
                        placeholder = { Text("Example: 123456") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { viewModel.fillSamplePassword() }) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Generate sample password",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val scoreColor by animateColorAsState(
                        targetValue = when {
                            analysis.score < 40 -> MaterialTheme.colorScheme.error
                            analysis.score < 75 -> Color(0xFFF59E0B)
                            else -> Color(0xFF10B981)
                        },
                        label = "scoreColor"
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Strength: ${analysis.label}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = scoreColor
                        )
                        Text(
                            text = "Score: ${analysis.score}/100",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = scoreColor
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { analysis.score / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .testTag("password_score_progress"),
                        color = scoreColor,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    if (analysis.suggestions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Suggestions:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                analysis.suggestions.forEach {
                                    Text(
                                        text = "• $it",
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.generateHashDemo() },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("hash_salt_button")
                        ) {
                            Text("Hash + Salt")
                        }

                        OutlinedButton(
                            onClick = { viewModel.runSafeGuessingSimulation() },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("lab_simulation_button")
                        ) {
                            Text("Lab Simulation")
                        }
                    }

                    AnimatedVisibility(visible = simulationResult.isNotEmpty()) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("simulation_result_card"),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = simulationResult,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- HASH + SALT DEMO CARD ---
        if (selectedModuleId == "all" || selectedModuleId == "password") {
            item {
                hashDemo?.let { demo ->
                    LabCard(
                        title = "🧂 Hash + Salt Demo",
                        subtitle = "Educational demonstration"
                    ) {
                        Text(
                            text = "Dummy password: ${demo.password}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        CodeSnippetBox(label = "Salt (16 random bytes hex):", code = demo.salt)

                        Spacer(modifier = Modifier.height(8.dp))

                        CodeSnippetBox(label = "SHA-256 (Salt + Password):", code = demo.hash)

                        if (demo.md5Hash.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            CodeSnippetBox(label = "Legacy MD5 (Unsalted - Insecure):", code = demo.md5Hash)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "💡 A cryptographic salt makes identical passwords produce completely distinct hashes, defending against precomputed Rainbow Table lookup dictionaries.",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(10.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // --- NETWORK LAB CARD ---
        if (selectedModuleId == "all" || selectedModuleId == "network") {
            item {
                LabCard(
                    title = "🌐 Network Lab",
                    subtitle = "Local lab concepts & subnet tools"
                ) {
                    Text(
                        text = "• IP address  • Subnet  • Port  • TCP / UDP  • HTTP / HTTPS  • Local-device security",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "IPv4 Subnet Calculator:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = networkIp,
                            onValueChange = viewModel::updateNetworkIp,
                            label = { Text("IP Address") },
                            singleLine = true,
                            modifier = Modifier.weight(1.5f)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text("CIDR: /$networkCidr", style = MaterialTheme.typography.labelSmall)
                            Slider(
                                value = networkCidr.toFloat(),
                                onValueChange = { viewModel.updateNetworkCidr(it.toInt()) },
                                valueRange = 8f..32f,
                                steps = 23
                            )
                        }
                    }

                    subnetResult?.let { sub ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                SubnetRow("Netmask:", sub.netmask)
                                SubnetRow("Network ID:", sub.networkAddress)
                                SubnetRow("Broadcast:", sub.broadcastAddress)
                                SubnetRow("Usable Range:", "${sub.firstUsableHost} - ${sub.lastUsableHost}")
                                SubnetRow("Total Usable Hosts:", "${sub.totalHosts}")
                                SubnetRow("Scope:", if (sub.isPrivateIp) "RFC 1918 Private" else "Public Internet")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Common Security Ports & Vectors:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = portQuery,
                        onValueChange = viewModel::updatePortQuery,
                        placeholder = { Text("Filter port (e.g. 80, 22, SSH, HTTPS)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val filteredPorts = NetworkLab.commonSecurityPorts.filter {
                        portQuery.isBlank() ||
                                it.port.toString().contains(portQuery) ||
                                it.service.contains(portQuery, ignoreCase = true) ||
                                it.protocol.contains(portQuery, ignoreCase = true)
                    }

                    filteredPorts.take(3).forEach { port ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Port ${port.port}/${port.protocol} - ${port.service}",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    text = port.description,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "⚠️ Risk: ${port.securityImplication}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "🛡️ Hardening: ${port.recommendedFix}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF10B981)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- ANDROID SECURITY LAB CARD ---
        if (selectedModuleId == "all" || selectedModuleId == "android") {
            item {
                LabCard(
                    title = "📱 Android Security Lab",
                    subtitle = "Learn by inspecting your own APK"
                ) {
                    Text("• AndroidManifest.xml  • Permissions  • Activities")
                    Text("• Services  • WebView configuration  • Debug / release build differences")

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Live APK Configuration Checklist:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { viewModel.runAndroidAudit(context) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Re-audit APK"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    auditItems.forEach { audit ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (audit.isSecure)
                                    MaterialTheme.colorScheme.surface
                                else
                                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = audit.checkName,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = if (audit.isSecure) Icons.Default.CheckCircle else Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = if (audit.isSecure) Color(0xFF10B981) else MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Text(
                                    text = "Status: ${audit.status}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = if (audit.isSecure) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = audit.description,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Best Practice: ${audit.recommendation}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- VULNERABILITY LAB CARD ---
        if (selectedModuleId == "all" || selectedModuleId == "vulnerability") {
            item {
                LabCard(
                    title = "🛡️ Vulnerability Lab",
                    subtitle = "Intentionally vulnerable training examples"
                ) {
                    Text("• Identify a weakness")
                    Text("• Understand why it happens")
                    Text("• Demonstrate safely")
                    Text("• Apply remediation")
                    Text("• Verify the fix")

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Select Training Topic:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        VulnerabilityLab.topics.forEach { topic ->
                            FilterChip(
                                selected = selectedVulnerability.id == topic.id,
                                onClick = { viewModel.selectVulnerabilityTopic(topic) },
                                label = { Text(topic.title) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${selectedVulnerability.title} (${selectedVulnerability.cweId})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.error
                                ) {
                                    Text(
                                        text = selectedVulnerability.severity,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onError,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = selectedVulnerability.vulnerabilityDescription,
                                style = MaterialTheme.typography.bodySmall
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            CodeSnippetBox(
                                label = "❌ Vulnerable Implementation:",
                                code = selectedVulnerability.vulnerableSnippet
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            CodeSnippetBox(
                                label = "✅ Remediated & Secure Code:",
                                code = selectedVulnerability.remediatedSnippet
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "💡 Fix: ${selectedVulnerability.fixExplanation}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (selectedVulnerability.id == "sqli") {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Interactive SQLi Payload Simulator:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = sqliInput,
                            onValueChange = viewModel::updateSqliInput,
                            label = { Text("Input Payload") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = sqliResult,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(10.dp),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // --- LAB REPORT CARD ---
        if (selectedModuleId == "all" || selectedModuleId == "report") {
            item {
                LabCard(
                    title = "📊 Lab Report",
                    subtitle = "Record what you learned"
                ) {
                    Text(
                        text = "Finding → Risk → Evidence → Fix → Verification",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showAddFindingDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Finding")
                        }

                        OutlinedButton(
                            onClick = {
                                val reportText = viewModel.generateMarkdownReport()
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Lab Security Report", reportText)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Report copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy Report")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    findings.forEach { finding ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = finding.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Target: ${finding.target} • Severity: ${finding.severity}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.toggleFindingVerified(finding.id) }
                                    ) {
                                        Icon(
                                            imageVector = if (finding.verified) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                                            contentDescription = "Toggle verification status",
                                            tint = if (finding.verified) Color(0xFF10B981) else MaterialTheme.colorScheme.outline
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteFinding(finding.id) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete finding",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "• Risk: ${finding.risk}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "• Evidence: ${finding.evidence}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "• Fix: ${finding.fix}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (finding.verified) "Status: ✅ Verified" else "Status: ⏳ Open",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (finding.verified) Color(0xFF10B981) else Color(0xFFF59E0B)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- CLEAR LAB BUTTON ---
        item {
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedButton(
                onClick = viewModel::clear,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("clear_lab_button"),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Clear Lab", fontWeight = FontWeight.Bold)
            }
        }
    }

    // --- ADD FINDING DIALOG ---
    if (showAddFindingDialog) {
        var findingTitle by remember { mutableStateOf("") }
        var findingSeverity by remember { mutableStateOf("High") }
        var findingRisk by remember { mutableStateOf("") }
        var findingEvidence by remember { mutableStateOf("") }
        var findingFix by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddFindingDialog = false },
            title = { Text("Add Lab Finding") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = findingTitle,
                        onValueChange = { findingTitle = it },
                        label = { Text("Finding Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = findingRisk,
                        onValueChange = { findingRisk = it },
                        label = { Text("Risk Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = findingEvidence,
                        onValueChange = { findingEvidence = it },
                        label = { Text("Evidence") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = findingFix,
                        onValueChange = { findingFix = it },
                        label = { Text("Remediation / Fix") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addFinding(
                            title = findingTitle,
                            severity = findingSeverity,
                            risk = findingRisk,
                            evidence = findingEvidence,
                            fix = findingFix
                        )
                        showAddFindingDialog = false
                    }
                ) {
                    Text("Save Finding")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFindingDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SubnetRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun CodeSnippetBox(label: String, code: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = code,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
private fun LabCard(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}
