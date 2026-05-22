package miwu.processor.mock

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.writeTo
import miwu.processor.MiwuProcessor
import java.io.File

internal class MockDeviceListProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : MiwuProcessor() {

    override fun onProcess(resolver: Resolver): List<KSAnnotated> {
        val filePath = options[OPTION_FILE_PATH] ?: return emptyList()
        val gradleEnabled = options[OPTION_ENABLED] != "false"
        val config = if (gradleEnabled) {
            loadConfig(filePath)
        } else {
            MockDeviceConfig(enabled = false, devices = emptyList())
        }

        generate(config.copy(enabled = gradleEnabled && config.enabled))
        return emptyList()
    }

    private fun loadConfig(filePath: String): MockDeviceConfig {
        val file = File(filePath)
        if (!file.isFile) {
            logger.error("$filePath not found")
            return MockDeviceConfig(enabled = false, devices = emptyList())
        }

        return parseYaml(file.readLines())
    }

    private fun parseYaml(lines: List<String>): MockDeviceConfig {
        var enabled = true
        var inDevices = false
        var currentDevice: MutableMap<String, String?>? = null
        val devices = mutableListOf<MutableMap<String, String?>>()

        lines.forEachIndexed { index, rawLine ->
            val line = rawLine.stripComment().trimEnd()
            if (line.isBlank()) return@forEachIndexed

            val indent = line.takeWhile(Char::isWhitespace).length
            val content = line.trim()

            if (indent == 0) {
                when {
                    content == "devices:" -> {
                        inDevices = true
                        currentDevice = null
                    }

                    content.startsWith("enabled:") -> {
                        enabled = content.valueAfterColon()?.toBooleanYaml()
                            ?: errorAt(index, "enabled must be true or false")
                    }

                    else -> errorAt(index, "unsupported root key")
                }
                return@forEachIndexed
            }

            if (!inDevices) errorAt(index, "mock device fields must be under devices")

            if (content.startsWith("- ")) {
                currentDevice = mutableMapOf<String, String?>()
                    .also(devices::add)

                val firstField = content.removePrefix("- ").trim()
                if (firstField.isNotEmpty()) {
                    currentDevice.putField(index, firstField)
                }
                return@forEachIndexed
            }

            val device = currentDevice ?: errorAt(index, "device field must start with '-'")
            device.putField(index, content)
        }

        return MockDeviceConfig(
            enabled = enabled,
            devices = devices.mapIndexedNotNull { index, device ->
                device.toMockDevice(index)
            }
        )
    }

    private fun MutableMap<String, String?>.putField(index: Int, content: String) {
        val key = content.substringBefore(':').trim()
        if (key == content) errorAt(index, "expected key: value")
        if (key !in DEVICE_KEYS) errorAt(index, "unsupported device key: $key")
        this[key] = content.valueAfterColon()
    }

    private fun Map<String, String?>.toMockDevice(index: Int): MockDevice? {
        fun required(key: String): String {
            return this[key]?.takeIf(String::isNotBlank)
                ?: errorAt(index, "device missing required key: $key")
        }

        return MockDevice(
            name = required("name"),
            did = required("did"),
            model = required("model"),
            specType = required("specType"),
            isOnline = this["isOnline"]?.toBooleanYaml()
                ?: true,
            mac = this["mac"]?.takeIf(String::isNotBlank)
                ?: DEFAULT_MAC,
            uid = this["uid"]?.takeIf(String::isNotBlank)
                ?: DEFAULT_UID,
            roomName = this["roomName"]?.takeIf(String::isNotBlank),
        )
    }

    private fun generate(config: MockDeviceConfig) {
        FileSpec.builder(GENERATED_PACKAGE, OBJECT_NAME)
            .addType(
                TypeSpec.objectBuilder(OBJECT_NAME)
                    .addProperty(
                        PropertySpec.builder("enabled", Boolean::class, KModifier.CONST)
                            .initializer("%L", config.enabled)
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder(
                            "devices",
                            List::class.asClassName().parameterizedBy(MiotDevice)
                        )
                            .initializer(createDevicesCode(config.devices))
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder(
                            "rooms",
                            Map::class.asClassName().parameterizedBy(String::class.asClassName(), String::class.asClassName())
                        )
                            .initializer(createRoomsCode(config.devices))
                            .build()
                    )
                    .build()
            )
            .build()
            .writeTo(codeGenerator = codeGenerator, aggregating = false)
    }

    private fun createDevicesCode(devices: List<MockDevice>): CodeBlock {
        if (devices.isEmpty()) return CodeBlock.of("emptyList()")

        return CodeBlock.builder()
            .add("listOf(\n")
            .indent()
            .apply {
                devices.forEach { device ->
                    add(
                        "%M(\n",
                        MockMiotDevice
                    )
                    indent()
                    add("name = %S,\n", device.name)
                    add("did = %S,\n", device.did)
                    add("model = %S,\n", device.model)
                    add("specType = %S,\n", device.specType)
                    add("isOnline = %L,\n", device.isOnline)
                    add("mac = %S,\n", device.mac)
                    add("uid = %S,\n", device.uid)
                    unindent()
                    add("),\n")
                }
            }
            .unindent()
            .add(")")
            .build()
    }

    private fun createRoomsCode(devices: List<MockDevice>): CodeBlock {
        val rooms = devices.mapNotNull { device ->
            device.roomName?.let { roomName -> device.did to roomName }
        }
        if (rooms.isEmpty()) return CodeBlock.of("emptyMap()")

        return CodeBlock.builder()
            .add("mapOf(\n")
            .indent()
            .apply {
                rooms.forEach { (did, roomName) ->
                    add("%S to %S,\n", did, roomName)
                }
            }
            .unindent()
            .add(")")
            .build()
    }

    private fun String.valueAfterColon(): String? {
        return substringAfter(':', missingDelimiterValue = "")
            .trim()
            .takeIf(String::isNotEmpty)
            ?.unquote()
    }

    private fun String.stripComment(): String {
        var singleQuoted = false
        var doubleQuoted = false

        forEachIndexed { index, char ->
            when (char) {
                '\'' -> if (!doubleQuoted) singleQuoted = !singleQuoted
                '"' -> if (!singleQuoted && (index == 0 || this[index - 1] != '\\')) {
                    doubleQuoted = !doubleQuoted
                }

                '#' -> if (!singleQuoted && !doubleQuoted) {
                    return substring(0, index)
                }
            }
        }

        return this
    }

    private fun String.unquote(): String {
        return when {
            length >= 2 && first() == '"' && last() == '"' ->
                substring(1, lastIndex).replace("\\\"", "\"").replace("\\\\", "\\")

            length >= 2 && first() == '\'' && last() == '\'' ->
                substring(1, lastIndex).replace("''", "'")

            else -> this
        }
    }

    private fun String.toBooleanYaml(): Boolean? {
        return when (lowercase()) {
            "true" -> true
            "false" -> false
            else -> null
        }
    }

    private fun errorAt(index: Int, message: String): Nothing {
        error("mock-devices.yaml line ${index + 1}: $message")
    }

    private data class MockDeviceConfig(
        val enabled: Boolean,
        val devices: List<MockDevice>,
    )

    private data class MockDevice(
        val name: String,
        val did: String,
        val model: String,
        val specType: String,
        val isOnline: Boolean,
        val mac: String,
        val uid: String,
        val roomName: String?,
    )

    private companion object {
        private const val OPTION_ENABLED = "miwu.mock.enabled"
        private const val OPTION_FILE_PATH = "miwu.mock.filePath"
        private const val GENERATED_PACKAGE = "com.github.miwu.mock"
        private const val OBJECT_NAME = "GeneratedMockDevices"
        private const val DEFAULT_MAC = "00:00:00:00:00:00"
        private const val DEFAULT_UID = "114514"
        private val DEVICE_KEYS = setOf(
            "name",
            "did",
            "model",
            "specType",
            "isOnline",
            "mac",
            "uid",
            "roomName",
        )
        private val MiotDevice = ClassName("miwu.miot.model.miot", "MiotDevice")
        private val MockMiotDevice = MemberName("miwu.support.mock", "MockMiotDevice")
    }
}
