package junit.filter;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class Assumptions {
	public static void requires(String... requiredFacets) {
		for (String requiredFacet : requiredFacets) {
			assumeTrue(hasFacet(requiredFacet));
		}
	}
	
	private static boolean hasFacet(String facet) {
		String prop = System.getProperty("activeFacets");
		if (prop == null || prop.isEmpty()) {
			return false;
		}
		String[] activeFacets = prop.split(",");
		for (String activeFacet : activeFacets) {
			if (facet.equals(activeFacet)) {
				return true;
			}
		}
		return false;
	}
	
	private Assumptions() {
		super();
	}
}
