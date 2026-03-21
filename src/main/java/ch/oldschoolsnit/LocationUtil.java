package ch.oldschoolsnit;

import ch.oldschoolsnit.records.BasicLocation;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.WorldView;

public class LocationUtil
{
	public static BasicLocation getLocation(Client client) {
		var wv = client.getLocalPlayer().getWorldView();
		var loc = client.getLocalPlayer().getWorldLocation();

		if (wv == null || loc == null) {
			return null;
		}

		var worldViewId = wv.getId();
		var isOnBoat = worldViewId != WorldView.TOPLEVEL;
		int plane = loc.getPlane();

		if(isOnBoat){
			var we = client.getTopLevelWorldView().worldEntities().byIndex(worldViewId);
			var worldPoint = WorldPoint.fromLocalInstance(client, we.getLocalLocation());
			int x = worldPoint.getX();
			int y = worldPoint.getY();
			return new BasicLocation(x, y, plane);
		}
		else {
			int x = loc.getX();
			int y = loc.getY();
			return new BasicLocation(x, y, plane);
		}
	}
}
