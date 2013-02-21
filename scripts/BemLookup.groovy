import org.codehaus.groovy.grails.cli.CommandLineHelper

import java.nio.file.FileSystems
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes

includeTargets << grailsScript("_GrailsInit")

includeTargets << grailsScript("_GrailsArgParsing")

target(main: "Lookup the usages of bem block") {
    // Getting block name
    String blockName = null
    try {
        blockName = argsMap.params.first()
    } catch (NoSuchElementException e) {
        def inputHelper = new CommandLineHelper()
        while(!blockName)
            blockName = inputHelper.userInput( "Please enter the block name to lookup, e.g. 'block':" )
    }
    if (blockName.startsWith("b-") || blockName.startsWith("e-")) {
        blockName = blockName.substring(2)
    }

    def printInFile = {File f->
        println()
        println f
        f.text.split("\n").eachWithIndex {line, i->
            if(line.contains(blockName)) {
                println "${i+1}\t:\t${line}"
            }
        }
    }

    println "Looking up b-${blockName}..."

    FileSystems.getDefault().getPathMatcher("glob:*Resources.groovy")

    println "Resource config:"

    for(Path p in Files.newDirectoryStream(Paths.get("${basedir}/grails-app/conf"), "*Resources.groovy")) if(p.toFile().text.indexOf(blockName)>0) {
        printInFile(p.toFile())
    }
    println();

    println "Views:"
    LookupFiles collector = new LookupFiles()
    collector
            .lookup(["gsp"], blockName)
            .walk(Paths.get("${basedir}/grails-app/views"))
            .each {
        printInFile(it.toFile())
    }
    println()

    println "Static resources:"
    collector
            .lookup(["css", "less", "sass", "scss", "styl", "coffee", "js", "html"], blockName)
            .walk(Paths.get("${basedir}/web-app"))
            .each {
        printInFile(it.toFile())
    }
    println()

    println "Done"
}

setDefaultTarget(main)

import static java.nio.file.FileVisitResult.*;

class LookupFiles
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
        if (exts.any {filename.endsWith(".${it}")} && file.toFile().text.indexOf(includes)>=0) {
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
}