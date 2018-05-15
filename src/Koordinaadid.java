import java.util.Objects;

public class Koordinaadid {
	private int x;
	private int y;
	public Koordinaadid(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	@Override
	public boolean equals(Object k) {
		if (k == this) {
			return true;
		}
		if (!(k instanceof Koordinaadid)) {
			return false;
		}
		Koordinaadid koord = (Koordinaadid) k;
		return this.x == koord.x && this.y == koord.y;
	}
	@Override
	public int hashCode() {
		return 31 + Objects.hash(this.x, this.y);
	}
	@Override
	public String toString() {
		return "Koordinaadid{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}