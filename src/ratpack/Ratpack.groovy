import com.google.common.io.Resources
import com.openmind.app.ConfigSlurperConfigSource

import static groovy.json.JsonOutput.prettyPrint
import static groovy.json.JsonOutput.toJson

import static ratpack.groovy.Groovy.ratpack


ratpack {
    serverConfig {
        // Use Groovy configuration.
        add new ConfigSlurperConfigSource('''\
            app {
                message = 'Ratpack swings!'
        }''')

        // Use external Groovy configuration script file.
        add new ConfigSlurperConfigSource(Resources.getResource('application.groovy'), 'development')

        require '/app', SimpleConfig
    }

    handlers {
        get('configprops') { SimpleConfig config ->
            render(prettyPrint(toJson(config)))
        }
    }
}

// Simple configuration.
class SimpleConfig {
    String message
    String serverName
    Integer serverPort
}
