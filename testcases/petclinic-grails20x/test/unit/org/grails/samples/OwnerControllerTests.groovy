package org.grails.samples

class OwnerControllerTests extends grails.test.ControllerUnitTestCase
{

  void testAddGET()
  {
    def controller = newInstance()

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
    def controller = newInstance()
    mockDomain(Owner)
    controller.request.method = 'POST'

    controller.add()

    assertEquals "add", renderArgs.view
    assertNotNull renderArgs.model.ownerBean
  }

  void testValidOwner()
  {
    def controller = newInstance()
    mockDomain(Owner)

    controller.params.owner = [firstName: 'fred',
            lastName: 'flintstone',
            address: 'rocky street',
            city: 'dinoville',
            telephone: '347239873']

    controller.add()

    assertEquals "show", redirectArgs.action
  }

  void testFindNoResults()
  {
    def controller = newInstance()
    mockDomain(Owner)
    controller.request.method = 'POST'

    def model = controller.find.call()
    assertEquals 'owners.not.found', model?.message
  }

  void testFindOneResult()
  {
    mockDomain(Owner, [new Owner(lastName: "flintstone")])

    def controller = newInstance()
    controller.request.method = 'POST'
    controller.params.lastName = 'flintstone'
    controller.find.call()

    assertEquals "show", redirectArgs.action
    assertEquals 1L, redirectArgs.id
  }

  void testFindManyResults()
  {
    mockDomain(Owner, [new Owner(lastName: "flintstone"), new Owner(lastName: "flintstone")])

    def controller = newInstance()
    controller.request.method = 'POST'
    controller.params.lastName = 'flintstone'
    controller.find.call()


    assertEquals "selection", renderArgs.view
    assertNotNull renderArgs.model.owners
    assertEquals 2, renderArgs.model.owners.size()
  }

}