import org.springframework.transaction.annotation.Transactional

/**
 * This checks how Spring's Transactional annotation works (it shall not modify the AST during compilation)
 */
@Transactional
class CopyService {
    def copyBook() {
        println "called copyBook"
    }
}
