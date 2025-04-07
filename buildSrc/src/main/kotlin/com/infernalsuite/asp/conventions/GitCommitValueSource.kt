package com.infernalsuite.asp.conventions

import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import java.io.Serializable
import javax.inject.Inject

// Thanking https://stackoverflow.com/a/78542879 for this solution. I just ported it to kotlin
abstract class GitCommitValueSource: ValueSource<String, ValueSourceParameters.None>, Serializable {
    @Inject
    abstract fun getExecOperations(): ExecOperations

    override fun obtain(): String {
        val output = ByteArrayOutputStream()
        getExecOperations().exec {
            commandLine = listOf("git", "rev-parse", "--verify", "HEAD")
            standardOutput = output
        }
        // Return the output as a trimmed string to exclude any trailing newline characters
        return String(output.toByteArray()).trim()
    }
}
