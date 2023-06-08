package ch.oldschoolsnit.records;

public class UserLocation
{
	private int x_coord;
	private int y_coord;
	private String apiKey;

	private Long accountHash;

	public UserLocation(int xCoord, int yCoord, String apiKey, Long accountHash)
	{
		x_coord = xCoord;
		y_coord = yCoord;
		this.apiKey = apiKey;
		this.accountHash = accountHash;
	}
}
