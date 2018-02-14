package com.openmind.app

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import ratpack.config.ConfigSource
import ratpack.file.FileSystemBinding

import java.nio.file.Path

@CompileStatic
class ConfigSlurperConfigSource implements ConfigSource {

    private final String configScript

    private final URL scriptUrl

    private final String environment

    ConfigSlurperConfigSource(final String configScript) {
        this(configScript, '')
    }

    ConfigSlurperConfigSource(final String configScript, final String environment) {
        this.configScript = configScript
        this.environment = environment
    }

    ConfigSlurperConfigSource(final Path configPath) {
        this(configPath, '')
    }

    ConfigSlurperConfigSource(final Path configPath, final String environment) {
        this(configPath.toUri(), environment)
    }

    ConfigSlurperConfigSource(final URI configUri) {
        this(configUri, '')
    }

    ConfigSlurperConfigSource(final URI configUri, final String environment) {
        this(configUri.toURL(), environment)
    }

    ConfigSlurperConfigSource(final URL configUrl) {
        this(configUrl, '')
    }

    ConfigSlurperConfigSource(final URL configUrl, final String environment) {
        this.scriptUrl = configUrl
        this.environment = environment
    }

    @Override
    ObjectNode loadConfigData(ObjectMapper objectMapper, FileSystemBinding fileSystemBinding) throws Exception {

        // Create ConfigSlurper for given environment.
        final ConfigSlurper configSlurper = new ConfigSlurper(environment)

        // Read configuration.
        final ConfigObject configObject = configScript ? configSlurper.parse(configScript) : configSlurper.parse(scriptUrl)

        // Transform configuration to node tree
        final ObjectNode rootNode = objectMapper.createObjectNode()

        populate(rootNode, configObject)

        rootNode
    }

    @CompileDynamic
    private populate(ObjectNode node, ConfigObject config) {
        // Loop through configuration.
        // ConfigObject also implements Map interface,
        // so we can loop through key/value pairs.
        config.each { key, value ->
            // Value is another configuration,
            // this means the nested configuration
            // block.
            if (value instanceof Map) {
                populate(node.putObject(key), value)
            } else {
                // If value is a List we convert it to
                // an array node.
                if (value instanceof List) {
                    final ArrayNode listNode = node.putArray(key)

                    value.each { listValue ->
                        listNode.add(listValue)
                    }
                } else {
                    // Put key/value pair in node.
                    node.put(key, value)
                }
            }
        }
    }
}
