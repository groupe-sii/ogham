package mock.bean;

import mock.util.bean.assetinventory.AssetInventory

class AssetInventoryGraphBuilder {
			
	static shipRegistry() {
		ObjectGraphBuilder builder = new ObjectGraphBuilder()
		builder.classNameResolver = "mock.util.bean.assetinventory"
		
		AssetInventory shipRegistry = builder.assetInventory() {
			ship ( name: "Sea Spirit", destination:"Chiba") {
				crewMember(firstName:"Michael", lastName:"Curiel",age:43)
				crewMember(firstName:"Sean", lastName:"Parker",age:28)
				crewMember(firstName:"Lillian", lastName:"Zimmerman",age:32)
				cargo(type:"Cotton", tons:5.4) {
					cargoOrder ( buyer: "Rei Hosokawa",city:"Yokohama",price:34000)
				}
				cargo(type:"Olive Oil", tons:3.0) {
					cargoOrder ( buyer: "Hirokumi Kasaya",city:"Kobe",price:27000)
				}
			}
			ship ( name: "Calypso I", destination:null) {
				crewMember(firstName:"Eric", lastName:"Folkes",age:35)
				crewMember(firstName:"Louis", lastName:"Lessard",age:22)
				cargo(type:"Oranges", tons:2.4) {
					cargoOrder ( buyer: "Gregory Schmidt",city:"Manchester",price:62000)
				}
			}
			ship ( name: "Desert Glory", destination:"Los Angeles")
			{
				crewMember(firstName:"Michelle", lastName:"Kindred",age:null)
				crewMember(firstName:"Kathy", lastName:"Parker",age:21)
				cargo(type:"Timber", tons:4.8) 	{
					cargoOrder ( buyer: "Carolyn Cox",city:"Sacramento",price:18000)
				}
			}
		}
		return shipRegistry
	}
}
