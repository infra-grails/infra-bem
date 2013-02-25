package infra.bem

import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.util.regex.Matcher
import java.util.regex.Pattern

import static java.nio.file.FileVisitResult.CONTINUE;

class BemManagerService {

    static transactional = false

    void lookupBlock(String basedir, String blockName) {
        Pattern ptrn = getBlockPattern(blockName)

        println "Looking up b-${blockName}..."

        println "Resource config:"

        for(Path p in Files.newDirectoryStream(Paths.get("${basedir}/grails-app/conf"), "*Resources.groovy")) if(p.toFile().text.indexOf(blockName)>0) {
            // printInFile(p.toFile())
        }
        println();

        println "Views:"
                lookup(["gsp"], blockName)
                .printMatches(Paths.get("${basedir}/grails-app/views"), ptrn)
        println()

        println "Static resources:"
                lookup(["css", "less", "sass", "scss", "styl", "coffee", "js", "html"], blockName)
                .printMatches(Paths.get("${basedir}/web-app"), ptrn)
        println()
    }

    void moveBlock(String basedir, String blockName, String moveToBlockName) {
        Pattern ptrn = getBlockPattern(blockName)

        println "Renaming b-${blockName} to b-${moveToBlockName}..."

        println "Views:"
        lookup(["gsp"], blockName)
                .walk(Paths.get("${basedir}/grails-app/views")).each{Path path->
            replaceInFile(path, ptrn, moveToBlockName)
        }
        println()
    }

    private void replaceInFile(Path filePath, Pattern pattern, String moveTo) {
        StringBuffer buffer = new StringBuffer()
        File file = filePath.toFile()
        file.eachLine {
            if (it =~ pattern) {
                replaceInLine(buffer, pattern, it, moveTo)
                buffer.append("\n")
            } else {
                buffer.append(it).append("\n")
            }
        }
        file.text = buffer.toString()
    }

    void replaceInLine(StringBuffer s, Pattern p, String line, String replaceTo) {
        Matcher m = p.matcher(line)
        while (m.find()) {
            m.appendReplacement(s, m.group(1).concat(replaceTo).concat(m.group(3)))
        }
        m.appendTail(s)
    }

    LookupFiles lookup(Collection<String> extensions, String includes) {
        new LookupFiles().lookup(extensions, includes)
    }

    Pattern getBlockPattern(String blockName) {
        ~/([bm]-|["'\s\{;]|^)(${blockName})([_"'\s\(\{]|$)/
    }

    public class LookupFiles
    extends java.nio.file.SimpleFileVisitor<Path> {

        private Collection<String> exts = []
        private List<Path> files = new LinkedList<>()
        private String includes

        LookupFiles lookup(Collection<String> extensions, String includes) {
            exts = extensions
            this.includes = includes
            files = new LinkedList<>()
            this
        }

        List<Path> getFiles() {
            files
        }

        @Override
        public FileVisitResult visitFile(Path file,
                                         BasicFileAttributes attr) {

            String filename = file.toString()
            if (exts.any { filename.endsWith(".${it}") } && file.toFile().text.indexOf(includes) >= 0) {
                files.add(file)
            }

            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file,
                                               IOException exc) {
            System.err.println(exc);
            return CONTINUE;
        }

        List<Path> walk(Path root) {
            Files.walkFileTree(root, this);
            files
        }

        void printMatches(Path root, Pattern p) {
            walk(root).each {Path path->
                File f = path.toFile()
                int i = 0
                f.eachLine {
                    ++i
                    if(it =~ p) {
                        println "${i}   :   ${it}"
                    }
                }
            }
        }
    }
}
