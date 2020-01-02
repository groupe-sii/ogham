package oghamcore.it.core.util.bean

import static mock.bean.AssetInventoryGraphBuilder.shipRegistry

import fr.sii.ogham.core.util.bean.MapRecursiveBeanReadWrapperFactory
import fr.sii.ogham.core.util.bean.RecursiveBeanReadWrapper
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class RecursiveBeanReadMethodBeanWrapperSpec extends Specification {
	def "wrap objects (#shipIndex #crewIndex #cargoIndex) recursively"() {
		given:
			def shipRegistry = new RecursiveBeanReadWrapper(shipRegistry())
			
		when:
			def wrappedShip = shipRegistry.getPropertyValue('ships').getPropertyValue(''+shipIndex)
			def wrappedCrewMember = wrappedShip.getPropertyValue('crewMembers').getPropertyValue(''+crewIndex)
			def wrappedCargo = wrappedShip.getPropertyValue('cargos').getPropertyValue(''+cargoIndex)
			def wrappedCargoOrder = wrappedCargo.getPropertyValue('cargoOrder')
			
		then:
			wrappedShip.getPropertyValue('name') == expectedShip
			wrappedCrewMember.getPropertyValue('firstName')+' '+wrappedCrewMember.getPropertyValue('lastName') == expectedCrew
			wrappedCargo.getPropertyValue('type') == expectedCargo
			wrappedCargoOrder.getPropertyValue('buyer') == expectedCargoOrder
			
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
	
	def "wrap objects (#shipIndex #crewIndex #cargoIndex) recursively into maps"() {
		given:
			def shipRegistry = new RecursiveBeanReadWrapper(shipRegistry(), new MapRecursiveBeanReadWrapperFactory())
			
		when:
			def wrappedShip = shipRegistry.getPropertyValue('ships').get(''+shipIndex)
			def wrappedCrewMember = wrappedShip.get('crewMembers').get(''+crewIndex)
			def wrappedCargo = wrappedShip.get('cargos').get(''+cargoIndex)
			def wrappedCargoOrder = wrappedCargo.get('cargoOrder')
			
		then:
			wrappedShip.get('name') == expectedShip
			wrappedCrewMember.get('firstName')+' '+wrappedCrewMember.get('lastName') == expectedCrew
			wrappedCargo.get('type') == expectedCargo
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

}