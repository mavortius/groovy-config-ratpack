app {
    serverPort = 9000
}

environments {
    development {
        app {
            serverName = 'local'
        }
    }
    production {
        app {
            serverName = 'cloud'
            serverPort = 80
        }
    }
}