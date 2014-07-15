package org.grails.samples

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.domain.DomainClassUnitTestMixin

@TestFor(OwnerController)
@TestMixin(DomainClassUnitTestMixin)
class OwnerControllerTests
{

  void testAddGET()
  {
    controller.request.method = 'GET'
    def model = controller.add()

    assertNotNull model.ownerBean
    assertTrue model.ownerBean instanceof Owner
  }

//  void testShow()
//  {
//    def controller = newInstance()
//
//    controller.request.method = 'GET'
//    controller.params.id = "1"
//    def model = controller.show()
//    println "MODEL: " + model
//    assertTrue model == null
//  }


  void testAddInvalidOwner()
  {
    mockDomain(Owner)
    controller.request.method = 'POST'

    controller.add()

    assertEquals "/owner/add", view
    assertNotNull model.ownerBean
  }

  void testValidOwner()
  {
    mockDomain(Owner)

    controller.params.owner = [firstName: 'fred',
            lastName: 'flintstone',
            address: 'rocky street',
            city: 'dinoville',
            telephone: '347239873']

    controller.add()

    assertEquals "/owner/show/1", response.redirectedUrl
  }

  void testFindNoResults()
  {
    mockDomain(Owner)
    controller.request.method = 'POST'

    def model = controller.find.call()
    assertEquals 'owners.not.found', model?.message
  }

  void testFindOneResult()
  {
    mockDomain(Owner, [new Owner(lastName: "flintstone")])

    controller.request.method = 'POST'
    controller.params.lastName = 'flintstone'
    controller.find.call()

    assertEquals "show/1", response.redirectedUrl
  }

  void testFindManyResults()
  {
    mockDomain(Owner, [new Owner(lastName: "flintstone"), new Owner(lastName: "flintstone")])

    controller.request.method = 'POST'
    controller.params.lastName = 'flintstone'
    controller.find.call()

    assertEquals "selection", response.redirectedUrl
    assertEquals "selection", view
    assertNotNull model.owners
    assertEquals 2, model.owners.size()
  }

}