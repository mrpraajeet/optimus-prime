package dev.praajeet.optimus.util

import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

object CommandExecutor {
    fun execute(command: List<String>, timeoutSeconds: Long = 60): Result<String> {
        if (command.isEmpty()) {
            return Result.failure(IllegalArgumentException("Command list cannot be empty."))
        }
        return try {
            val processBuilder = ProcessBuilder(command).redirectErrorStream(true)
            val process = processBuilder.start()
            if (!process.waitFor(timeoutSeconds, TimeUnit.SECONDS)) {
                process.destroyForcibly()
                return Result.failure(TimeoutException("Command '${command.joinToString(" ")}' timed out after $timeoutSeconds seconds."))
            }
            val output = process.inputStream.bufferedReader().readText().trim()
            if (process.exitValue() == 0) {
                Result.success(output)
            } else {
                Result.failure(RuntimeException("Command '${command.joinToString(" ")}' failed with exit code ${process.exitValue()}:\n$output"))
            }
        } catch (e: IOException) {
            Result.failure(IOException("Failed to start command '${command.first()}'", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
