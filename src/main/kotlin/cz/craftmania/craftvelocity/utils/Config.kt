package cz.craftmania.craftvelocity.utils

import com.google.inject.Inject
import com.moandjiezana.toml.Toml
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path


class Config {

    private var dataDir: Path? = null
    private var tomlFile: Toml? = null

    // Values
    var DEBUG_ENABLED = false

    @Inject
    constructor(dataDir: Path){
        this.dataDir = dataDir
        loadFile()
        loadConfig()
    }

    private fun loadFile() {
        val dataDirectory: File = this.dataDir!!.toFile()
        if (!dataDirectory.exists()) {
            dataDirectory.mkdir()
        }

        val dataFile = File(dataDirectory, "config.toml")
        if (!dataFile.exists()) {
            try {
                val inputStream = this.javaClass.getResourceAsStream("/config.toml")
                if (inputStream != null) {
                    Files.copy(inputStream, dataFile.toPath())
                } else {
                    throw RuntimeException("ERROR: Can't read default configuration file (permissions/filesystem error?)")
                }
            } catch (e: IOException) {
                throw RuntimeException("ERROR: Can't write default configuration file (permissions/filesystem error?)")
            }
        }
        this.tomlFile = Toml().read(dataFile)
    }

    fun loadConfig() {
        this.DEBUG_ENABLED = this.tomlFile!!.getBoolean("plugin.debug", false)
    }
}