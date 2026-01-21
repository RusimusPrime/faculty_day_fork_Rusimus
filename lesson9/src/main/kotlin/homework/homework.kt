package main

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

fun createZipArchive(sourceDir: File, outputFile: File) {
    // Проверяем, существует ли директория
    if (!sourceDir.exists() || !sourceDir.isDirectory) {
        throw IllegalArgumentException("Каталог не существует: ${sourceDir.path}")
    }

    // Создаем ZIP архив
    ZipOutputStream(FileOutputStream(outputFile)).use { zipOut ->
        // Обходим все файлы в директории
        val files = sourceDir.walk()
        for (file in files) {
            if (file.isFile && (file.extension == "txt" || file.extension == "log")) {
                val relativePath = sourceDir.toPath().relativize(file.toPath()).toString()
                val entry = ZipEntry(relativePath)

                try {
                    FileInputStream(file).use { input ->
                        zipOut.putNextEntry(entry)
                        input.copyTo(zipOut)
                        zipOut.closeEntry()
                        println("Добавлен: $relativePath (${file.length()} байт)")
                    }
                } catch (e: Exception) {
                    println("Ошибка при обработке ${file.name}: ${e.message}")
                }
            }
        }
    }
}

fun main() {
    val sourceDir = File("project_data")
    val zipFile = File("archive.zip")

    try {
        createZipArchive(sourceDir, zipFile)
        println("Архив успешно создан!")
    } catch (e: Exception) {
        println("Ошибка: ${e.message}")
    }
}

