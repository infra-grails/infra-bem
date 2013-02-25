package infra.bem.manager

import java.nio.file.Files
import java.nio.file.Path

/**
 * @author alari
 * @since 2/25/13 7:08 PM
 */
class MatchingFilesManager extends FilesManager {
    MatchingFilesManager(String basedir, String baseBlock) {
        super(basedir)
        setBaseBlock(baseBlock)
    }

    void printConfMatches() {
        for (Path p in Files.newDirectoryStream(confPath, "*Resources.groovy")) if (p.toFile().text.indexOf(baseBlock) > 0) {
            printFilePathMatches(p)
        }
    }

    void printViewsMatches() {
        lookup(VIEWS_EXTENSIONS).walk(views).each {
            printFilePathMatches(it)
        }
    }

    void printStaticsMatches() {
        lookup(STATIC_EXTENSIONS).walk(statics).each {
            printFilePathMatches(it)
        }
    }

    void printFilePathMatches(Path path) {
        if (!pattern) {
            throw new IllegalStateException("You have to specify Pattern to use matchers")
        }
        println path
        File f = path.toFile()
        int i = 0
        f.eachLine {
            ++i
            if (it =~ pattern) {
                println "${i}   :   ${it}"
            }
        }
    }
}
