package infra.bem

import infra.bem.manager.MovingFilesManager
import spock.lang.Specification

class MovingFilesManagerSpec extends Specification {

    void "builds correct paths for view and statics"() {
        when:
        def fm = new MovingFilesManager("/n", "root", "test-again")

        then:
        fm.getBlockStatics("root").toString() == "/n/web-app/bem/root"
        fm.getBlockTemplate("root").toString() == "/n/grails-app/views/bem/_root.gsp"
        fm.getBlockTemplate("test-again").toString() == "/n/grails-app/views/bem/test/_again.gsp"
        fm.getBlockStatics("test-again").toString() == "/n/web-app/bem/test/again"
    }
}
