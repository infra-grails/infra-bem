package infra.bem.manager

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

    void modifyViews() {
        lookup(VIEWS_EXTENSIONS).walk(views).each {Path path->
            replaceInFile(path)
        }
    }

    private void replaceInFile(Path filePath) {
        StringBuffer buffer = new StringBuffer()
        File file = filePath.toFile()
        file.eachLine {
            if (it =~ pattern) {
                replaceInLine(buffer, it)
                buffer.append("\n")
            } else {
                buffer.append(it).append("\n")
            }
        }
        file.text = buffer.toString()
    }

    void replaceInLine(StringBuffer s, String line) {
        Matcher m = pattern.matcher(line)
        while (m.find()) {
            m.appendReplacement(s, m.group(1).concat(moveTo).concat(m.group(3)))
        }
        m.appendTail(s)
    }
}
