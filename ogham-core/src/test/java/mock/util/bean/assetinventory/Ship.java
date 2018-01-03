package mock.util.bean.assetinventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ship {
	private String name;
	private List<CrewMember> crewMembers = new ArrayList<>();
	private Map<String, CrewMember> crewMembersByName = new HashMap<>();
	private String destination;
	private List<Cargo> cargos= new ArrayList<>();
	

	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<CrewMember> getCrewMembers() {
		return crewMembers;
	}
	public void setCrewMembers(List<CrewMember> crewMembers) {
		this.crewMembers = crewMembers;
	}
	public Map<String, CrewMember> getCrewMembersByName() {
		if(crewMembersByName.isEmpty()) {
			for(CrewMember member : crewMembers) {
				this.crewMembersByName.put(member.getFirstName()+" "+member.getLastName(), member);
			}
		}
		return crewMembersByName;
	}
	public void setCrewMembersByName(Map<String, CrewMember> crewMembersByName) {
	}
	public List<Cargo> getCargos() {
		return cargos;
	}
	public void setCargos(List<Cargo> cargos) {
		this.cargos = cargos;
	}
	
	
	
	
}