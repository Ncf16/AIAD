package agents;

public class CloneObj {
	public CloneObj clone() {
		try {
			return (CloneObj) super.clone();
		} catch (CloneNotSupportedException err) {
			return null;
		}
	}
}
