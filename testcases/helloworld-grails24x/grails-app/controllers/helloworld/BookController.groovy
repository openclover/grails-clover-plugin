package helloworld


class BookController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]


    static boolean helloWorld() {
        println "Hello World"
        return true;
    }

    def add = {
        if (request.method == 'GET') {
            [bookBean: new Book()]
        } else {
            def book = new Book(params['title'])
            if (book.save()) {
                redirect action: 'show', id: book.id
            } else {
                render view: 'add', model: [bookBean: book]
            }
        }
    }

    def show = {
        def book = Book.get(params.id)
        if (book)
            [bookBean: book]
        else
            response.sendError 404
    }

    def edit = {
        def book = Book.get(params.id)
        if (request.method == 'GET') {
            render view: 'add', model: [bookBean: book]
        } else {
            book.properties = params['title']
            if (book.save()) {
                redirect action: 'show', id: book.id
            } else {
                render view: 'add', model: [bookBean: book]
            }
        }
    }

    def find = {
        if (request.method == 'POST') {
            def books = Book.findAllByTitle(params.title)
            if (books) {
                if (books.size() > 1)
                    render view: 'selection', model: [books: books]
                else
                    redirect action: 'show', id: books[0].id
            } else {
                response.sendError(404)
            }
        }
    }
}
