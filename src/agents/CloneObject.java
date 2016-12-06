package agents;

public class CloneObject {
	public CloneObject clone() {
		try {
			return (CloneObject) super.clone();
		} catch (CloneNotSupportedException err) {
			return null;
		}
	}
}
