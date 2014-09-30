import grails.transaction.Transactional

/**
 * This is an example of Transactional service (entire class)
 */
@Transactional
class TransactionalAuthorService {
    def showAuthor() {
        println "called showAuthor"
    }

    def hideAuthor() {
        println "called hideAuthor"
    }
}

