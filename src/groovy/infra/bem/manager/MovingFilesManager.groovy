package infra.bem.manager

import static java.nio.file.StandardCopyOption.*;

import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Matcher

/**
 * @author alari
 * @since 2/25/13 7:10 PM
 */
class MovingFilesManager extends FilesManager {
    private final String moveTo

    MovingFilesManager(String basedir, String baseBlock, String moveTo) {
        super(basedir)
        setBaseBlock(baseBlock)
        this.moveTo = moveTo
    }

    private String getBlockPath(String block) {
        if (!block.contains("-")) {
            return ""
        }
        List<String> parts = block.split("-").toList()
        parts.pop()
        parts.join("/")
    }

    private String getBlockTail(String block) {
        if (!block.contains("-")) {
            return block
        }
        block.substring(block.lastIndexOf("-")+1)
    }

    Path getBlockTemplate(String block) {
        views.resolve("bem/"+getBlockPath(block)+"/_"+getBlockTail(block)+".gsp")
    }

    Path getBlockStatics(String block) {
        statics.resolve("bem/"+block.replace("-", "/"))
    }

    void modifyStatics() {
        Map<String,String> rename = [:]
        getBlockStatics(baseBlock).each {
            if(Files.isRegularFile(it)) {
                String fn = it.toFile().name
                move(getBlockStatics(baseBlock).resolve(fn), getBlockStatics(moveTo).resolve(fn))
                rename.put(getBlockPath(baseBlock)+"/"+fn, getBlockPath(moveTo)+"/"+fn)
            }
        }
        // TODO: rename in statics and resources
    }

    void modifyViews() {
        move(getBlockTemplate(baseBlock), getBlockTemplate(moveTo))
        lookup(VIEWS_EXTENSIONS).walk(views).each {Path path->
            replaceInFile(path)
        }
    }

    private void move(Path src, Path dest) {
        Files.move(src, dest, ATOMIC_MOVE)
    }

    void writeToFile(File file, StringBuffer buffer) {
        file.text = buffer.toString()
    }

    private void replaceInFile(Path filePath) {
        StringBuffer buffer = new StringBuffer()
        File file = filePath.toFile()
        file.eachLine {
            replaceAppend(it, buffer)
        }
        writeToFile(file, buffer)
    }

    void replaceAppend(String line, StringBuffer buffer) {
        if (line =~ pattern) {
            Matcher m = pattern.matcher(line)
            while (m.find()) {
                m.appendReplacement(buffer, m.group(1).concat(moveTo).concat(m.group(3)))
            }
            m.appendTail(buffer)
            buffer.append("\n")
        } else {
            buffer.append(line).append("\n")
        }
    }
}
