package infra.bem.manager

import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes


import static java.nio.file.FileVisitResult.CONTINUE;

/**
 * @author alari
 * @since 2/25/13 6:29 PM
 */
class LookupFiles extends java.nio.file.SimpleFileVisitor<Path> {

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
}