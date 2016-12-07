package broker;

public class Pair<L, R> {
	private L key;
	private R value;

	public Pair(L l, R r) {
		this.key = l;
		this.value = r;
	}

	public L getKey() {
		return key;
	}

	public void setKey(L key) {
		this.key = key;
	}

	public R getValue() {
		return value;
	}

	public void setValue(R value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Pair [key=" + key + ", value=" + value + "]";
	}

}
