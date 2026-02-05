import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

fun createZipArchive(sourceDir: File, outputFile: File) {
    if (!sourceDir.exists() || !sourceDir.isDirectory) {
        throw IllegalArgumentException("Каталог не существует: ${sourceDir.path}")
    }

    ZipOutputStream(FileOutputStream(outputFile)).use { zipOut ->

        val files = sourceDir.walkTopDown()
            .filter {
                it.isFile && (it.extension == "txt" || it.extension == "log")
            }

        for (file in files) {
            val relativePath = sourceDir.toPath()
                .relativize(file.toPath())
                .toString()

            val entry = ZipEntry(relativePath)

            FileInputStream(file).use { input ->
                zipOut.putNextEntry(entry)
                input.copyTo(zipOut)
                zipOut.closeEntry()
            }

        }
    }
}

fun main() {
    val sourceDir = File("lesson9/archived")

    val outputFile = File("archive.zip")

    try {
        createZipArchive(sourceDir, outputFile)
        println("Архив создан: ${outputFile.absolutePath}")
    } catch (e: Exception) {
        println("Ошибка: ${e.message}")
    }
}