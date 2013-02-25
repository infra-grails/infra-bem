package infra.bem

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Matcher
import java.util.regex.Pattern

class BemManagerService {

    static transactional = false

    static private final STATIC_EXTENSIONS = ["css", "less", "sass", "scss", "styl", "coffee", "js", "html"]
    static private final VIEWS_EXTENSIONS = ["gsp"]

    void lookupBlock(String basedir, String blockName) {
        Pattern ptrn = getBlockPattern(blockName)

        println "Looking up b-${blockName}..."

        println "Resource config:"

        for (Path p in Files.newDirectoryStream(Paths.get("${basedir}/grails-app/conf"), "*Resources.groovy")) if (p.toFile().text.indexOf(blockName) > 0) {
            printPathMatches(p, ptrn)
        }
        println();

        println "Views:"
        lookupViews(blockName).walk(Paths.get("${basedir}/grails-app/views")).each {
            printPathMatches(it, ptrn)
        }
        println()

        println "Static resources:"
        lookupStatic(blockName).walk(Paths.get("${basedir}/web-app")).each {
            printPathMatches(it, ptrn)
        }
        println()
    }

    void printPathMatches(Path path, Pattern p) {
        println path
        File f = path.toFile()
        int i = 0
        f.eachLine {
            ++i
            if (it =~ p) {
                println "${i}   :   ${it}"
            }
        }
    }

    void moveBlock(String basedir, String blockName, String moveToBlockName) {
        Pattern ptrn = getBlockPattern(blockName)

        println "Renaming b-${blockName} to b-${moveToBlockName}..."

        // find block resources in views
        // move the views
        // find block resources in web-app
        // collect the paths
        // move block resources
        // walk against all the resources and configs
        // replace old paths to new paths

        lookup(["gsp"], blockName).walk(Paths.get("${basedir}/grails-app/views")).each { Path path ->
            println "Replacing in file: ${path}"
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

    BemLookupFiles lookupStatic(String includes) {
        lookup(STATIC_EXTENSIONS, includes)
    }

    BemLookupFiles lookupViews(String includes) {
        lookup(VIEWS_EXTENSIONS, includes)
    }

    BemLookupFiles lookup(Collection<String> extensions, String includes) {
        new BemLookupFiles().lookup(extensions, includes)
    }

    Pattern getBlockPattern(String blockName) {
        ~/([bm]-|["'\s\{;]|^)(${blockName})([_"'\s\(\{]|$)/
    }

}
