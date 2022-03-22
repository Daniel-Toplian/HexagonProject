package board;

public class Move {
	private Hexagon hexFrom;
	private Hexagon hexTo;

	/**
	 * @param hexFrom The Hexagon to move (in it's original place).
	 * @param hexTo   The location of the Hexagon in the end.
	 */
	public Move(Hexagon hexFrom, Hexagon hexTo) {
		super();
		this.hexFrom = hexFrom;
		this.hexTo = hexTo;
	}

	public Hexagon getHexFrom() {
		return hexFrom;
	}

	public void setHexFrom(Hexagon hexFrom) {
		this.hexFrom = hexFrom;
	}

	public Hexagon getHexTo() {
		return hexTo;
	}

	public void setHexTo(Hexagon hexTo) {
		this.hexTo = hexTo;
	}

	public String toString() {
		return "Hex from: " + this.hexFrom.getStorageRow() + ", " + this.hexFrom.getStorageCol() + "\nHex to: "
				+ this.hexTo.getStorageRow() + ", " + this.hexTo.getStorageCol();

	}
}
