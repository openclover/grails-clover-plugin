package org.grails.sample

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import helloworld.grails30x.Room
import org.junit.Test

@Integration
@Rollback
public class MyIntegrationTests {

    void setupData() {
        new Room(title: 'Grails in Action').save(flush: true)
    }

    @Test
    void "test something"() {
        setupData()

        Room.count() == 1
    }
}