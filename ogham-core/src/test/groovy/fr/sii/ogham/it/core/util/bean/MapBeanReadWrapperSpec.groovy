package fr.sii.ogham.it.core.util.bean

import static fr.sii.ogham.mock.bean.AssetInventoryGraphBuilder.shipRegistry

import java.util.Map.Entry

import fr.sii.ogham.core.exception.util.InvalidPropertyException
import fr.sii.ogham.core.util.bean.MapBeanReadWrapper
import mock.util.bean.assetinventory.AssetInventory
import spock.lang.Specification

class MapBeanReadWrapperSpec extends Specification {
	def "wrap objects (#shipIndex #crewIndex #cargoIndex) into maps"() {
		given:
			AssetInventory shipRegistry = shipRegistry()
			
		when:
			def ship = shipRegistry.ships[shipIndex]
			def wrappedShip = new MapBeanReadWrapper(ship)
			def wrappedCrewMember = new MapBeanReadWrapper(ship.crewMembers[crewIndex])
			def cargo = ship.cargos[cargoIndex]
			def wrappedCargo = new MapBeanReadWrapper(cargo)
			def wrappedCargoOrder = new MapBeanReadWrapper(cargo.cargoOrder)
			
		then:
			wrappedShip instanceof Map
			wrappedShip.get('name') == expectedShip
			wrappedCrewMember instanceof Map
			wrappedCrewMember.get('firstName')+' '+wrappedCrewMember.get('lastName') == expectedCrew
			wrappedCargo instanceof Map
			wrappedCargo.get('type') == expectedCargo
			wrappedCargoOrder instanceof Map
			wrappedCargoOrder.get('buyer') == expectedCargoOrder
			
		where:
			shipIndex | crewIndex | cargoIndex || expectedShip    | expectedShipDestination | expectedCrew        | expectedCargo  | expectedCargoOrder
			0         | 0         | 0          || 'Sea Spirit'    | 'Chiba'                 | 'Michael Curiel'    | 'Cotton'       | 'Rei Hosokawa'
			0         | 1         | 0          || 'Sea Spirit'    | 'Chiba'                 | 'Sean Parker'       | 'Cotton'       | 'Rei Hosokawa'
			0         | 2         | 0          || 'Sea Spirit'    | 'Chiba'                 | 'Lillian Zimmerman' | 'Cotton'       | 'Rei Hosokawa'
			0         | 0         | 1          || 'Sea Spirit'    | 'Chiba'                 | 'Michael Curiel'    | 'Olive Oil'    | 'Hirokumi Kasaya'
			1         | 0         | 0          || 'Calypso I'     | null                    | 'Eric Folkes'       | 'Oranges'      | 'Gregory Schmidt'
			1         | 1         | 0          || 'Calypso I'     | null                    | 'Louis Lessard'     | 'Oranges'      | 'Gregory Schmidt'
			2         | 0         | 0          || 'Desert Glory'  | 'Los Angeles'           | 'Michelle Kindred'  | 'Timber'       | 'Carolyn Cox'
			2         | 1         | 0          || 'Desert Glory'  | 'Los Angeles'           | 'Kathy Parker'      | 'Timber'       | 'Carolyn Cox'
	}
	
	def "wrap map for ship (#shipIndex) into a map"() {
		given:
			AssetInventory shipRegistry = shipRegistry()
			
		when:
			def ship = shipRegistry.ships[shipIndex]
			def wrappedMap = new MapBeanReadWrapper(ship.crewMembersByName)
			def wrappedCrew = wrappedMap.get(crew)
			
		then:
			wrappedMap instanceof Map
			wrappedCrew.firstName+' '+wrappedCrew.lastName == expectedCrew
			wrappedCrew.age == expectedCrewAge
			
		where:
			shipIndex | crew                || expectedCrew        | expectedCrewAge
			0         | 'Michael Curiel'    || 'Michael Curiel'    | 43
			0         | 'Sean Parker'       || 'Sean Parker'       | 28
			0         | 'Lillian Zimmerman' || 'Lillian Zimmerman' | 32
			1         | 'Eric Folkes'       || 'Eric Folkes'       | 35
			1         | 'Louis Lessard'     || 'Louis Lessard'     | 22
			2         | 'Michelle Kindred'  || 'Michelle Kindred'  | null
			2         | 'Kathy Parker'      || 'Kathy Parker'      | 21
	}
	
	def "wrap collection for ship #shipIndex into a map"() {
		given:
			AssetInventory shipRegistry = shipRegistry()
			
		when:
			def ship = shipRegistry.ships[shipIndex]
			def wrappedMap = new MapBeanReadWrapper(ship.crewMembers)
			def wrappedCrew = wrappedMap.get(crewIndex)
			
		then:
			wrappedMap instanceof Map
			wrappedCrew.firstName+' '+wrappedCrew.lastName == expectedCrew
			
		where:
			shipIndex | crewIndex || expectedCrew
			0         | 0         || 'Michael Curiel'
			0         | 1         || 'Sean Parker'
			0         | 2         || 'Lillian Zimmerman'
			1         | 0         || 'Eric Folkes'
			1         | 1         || 'Louis Lessard'
			2         | 0         || 'Michelle Kindred'
			2         | 1         || 'Kathy Parker'
	}
	
	def "invalid property name (#propertyName)"() {
		given:
			AssetInventory shipRegistry = shipRegistry()
			
		when:
			new MapBeanReadWrapper(shipRegistry.ships[shipIndex]).get(propertyName)
			
		then:
			Exception ex = thrown(expectedException)
			ex.message == expectedMessage
			
		where:
			shipIndex | propertyName || expectedException        | expectedMessage
			0         | 'aaa'        || InvalidPropertyException | "No accessor for property 'aaa' on bean 'mock.util.bean.assetinventory.Ship'"
			0         | '0'          || InvalidPropertyException | "No accessor for property '0' on bean 'mock.util.bean.assetinventory.Ship'"
			1         | 'aaa'        || InvalidPropertyException | "No accessor for property 'aaa' on bean 'mock.util.bean.assetinventory.Ship'"
			1         | '0'          || InvalidPropertyException | "No accessor for property '0' on bean 'mock.util.bean.assetinventory.Ship'"
			2         | 'aaa'        || InvalidPropertyException | "No accessor for property 'aaa' on bean 'mock.util.bean.assetinventory.Ship'"
			2         | '0'          || InvalidPropertyException | "No accessor for property '0' on bean 'mock.util.bean.assetinventory.Ship'"
	}
	
	def "invalid property name (#propertyName) no fail"() {
		given:
			AssetInventory shipRegistry = shipRegistry()
			
		when:
			def value = new MapBeanReadWrapper(shipRegistry.ships[shipIndex], false).get(propertyName)
			
		then:
			value == null
			
		where:
			shipIndex | propertyName
			0         | 'aaa'       
			0         | '0'         
			1         | 'aaa'       
			1         | '0'         
			2         | 'aaa'       
			2         | '0'         
	}

	def "invalid map key (#crewName)"() {
		given:
			AssetInventory shipRegistry = shipRegistry()
			
		when:
			new MapBeanReadWrapper(shipRegistry.ships[shipIndex].crewMembersByName).get(crewName)
			
		then:
			Exception ex = thrown(expectedException)
			ex.message == expectedMessage
			
		where:
			shipIndex | crewName || expectedException        | expectedMessage
			0         | 'aaa'    || InvalidPropertyException | "No accessor for property 'aaa' on bean 'java.util.HashMap'"
			0         | '0'      || InvalidPropertyException | "No accessor for property '0' on bean 'java.util.HashMap'"
			1         | 'aaa'    || InvalidPropertyException | "No accessor for property 'aaa' on bean 'java.util.HashMap'"
			1         | '0'      || InvalidPropertyException | "No accessor for property '0' on bean 'java.util.HashMap'"
			2         | 'aaa'    || InvalidPropertyException | "No accessor for property 'aaa' on bean 'java.util.HashMap'"
			2         | '0'      || InvalidPropertyException | "No accessor for property '0' on bean 'java.util.HashMap'"
	}

	
	def "invalid map key (#crewName) no fail"() {
		given:
			AssetInventory shipRegistry = shipRegistry()
			
		when:
			def value = new MapBeanReadWrapper(shipRegistry.ships[shipIndex].crewMembersByName, false).get(crewName)
			
		then:
			value == null
			
		where:
			shipIndex | crewName
			0         | 'aaa'   
			0         | '0'     
			1         | 'aaa'   
			1         | '0'     
			2         | 'aaa'   
			2         | '0'     
	}
	
	def "null bean value (#propertyName)"() {
		given:
			def wrappedValue = new MapBeanReadWrapper(null)
			
		when:
			def value = wrappedValue.get(propertyName)
			
		then:
			value == expectedValue
		
		where:
			propertyName || expectedValue
			'aaa'        || null
			'0'          || null
			'null'       || null
	}
	
	def "can't wrap a primitive type (#primitiveValue)"() {
		when:
			new MapBeanReadWrapper(primitiveValue)
			
		then:
			thrown(IllegalArgumentException)
			
		where:
			primitiveValue         | exception
			'string'               | IllegalArgumentException
			10                     | IllegalArgumentException
			50.0f                  | IllegalArgumentException
			new BigDecimal("50.0") | IllegalArgumentException
			new BigInteger("50")   | IllegalArgumentException
	}
	
	def "use containsKey() on wrapped bean"() {
		given:
			def map = new MapBeanReadWrapper(shipRegistry().ships[0])
		
		when:
			def contains = map.containsKey(propertyName)
		
		then:
			contains == expected
			
		where:
			propertyName         | expected
			'name'               | true
			'crewMembers'        | true
			'crewMembersByName'  | true
			'destination'        | true
			'cargos'             | true
			'foo'                | false
	}
	
	def "use size() on wrapped bean"() {
		when:
			def map = new MapBeanReadWrapper(shipRegistry().ships[0])
		then:
			map.size()==6 // don't forget class
			map.isEmpty()==false
	}
	
	def "use keySet() on wrapped bean"() {
		when:
			def map = new MapBeanReadWrapper(shipRegistry().ships[0])
		then:
			map.keySet().containsAll(['name', 'crewMembers', 'crewMembersByName', 'destination', 'cargos', 'class'])
	}

	def "use entrySet() on wrapped bean"() {
		when:
			def map = new MapBeanReadWrapper(shipRegistry().ships[0])
			def keys = []
			for(Entry<String, Object> entry : map.entrySet()) {
				keys.add(entry.key)
				// TODO: check values too ?
			}
		then:
			keys.containsAll(['name', 'crewMembers', 'crewMembersByName', 'destination', 'cargos', 'class'])
	}
}