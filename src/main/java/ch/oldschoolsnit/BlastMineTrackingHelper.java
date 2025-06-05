package ch.oldschoolsnit;

import ch.oldschoolsnit.records.BlastMineVarbitChangeInfo;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.VarbitID;

public class BlastMineTrackingHelper
{
	private Boolean coal_loaded = false;
	private Boolean gold_loaded = false;
	private Boolean mithril_loaded = false;
	private Boolean adamantite_loaded = false;
	private Boolean runite_loaded = false;

	private int coal_count = 0;
	private int gold_count = 0;
	private int mithril_count = 0;
	private int adamantite_count = 0;
	private int runite_count = 0;


	public BlastMineVarbitChangeInfo varbitChangedHandler(VarbitChanged varbitChanged)
	{
		switch (varbitChanged.getVarbitId())
		{
			case VarbitID.LOVAKENGJ_ORE_COAL_BIGGER:
				if (!this.coal_loaded)
				{
					this.coal_loaded = true;
					this.coal_count = varbitChanged.getValue();
					return new BlastMineVarbitChangeInfo(453, 0);
				}
				else
				{
					var previousCount = this.coal_count;
					var newCount = varbitChanged.getValue();
					var delta = newCount - previousCount;
					this.coal_count = newCount;
					return new BlastMineVarbitChangeInfo(453, delta);
				}

			case VarbitID.LOVAKENGJ_ORE_GOLD_BIGGER:
				if (!this.gold_loaded)
				{
					this.gold_loaded = true;
					this.gold_count = varbitChanged.getValue();
					return new BlastMineVarbitChangeInfo(444, 0);
				}
				else
				{
					var previousCount = this.gold_count;
					var newCount = varbitChanged.getValue();
					var delta = newCount - previousCount;
					this.gold_count = newCount;
					return new BlastMineVarbitChangeInfo(444, delta);
				}

			case VarbitID.LOVAKENGJ_ORE_MITHRIL_BIGGER:
				if (!this.mithril_loaded)
				{
					this.mithril_loaded = true;
					this.mithril_count = varbitChanged.getValue();
					return new BlastMineVarbitChangeInfo(447, 0);
				}
				else
				{
					var previousCount = this.mithril_count;
					var newCount = varbitChanged.getValue();
					var delta = newCount - previousCount;
					this.mithril_count = newCount;
					return new BlastMineVarbitChangeInfo(447, delta);
				}

			case VarbitID.LOVAKENGJ_ORE_ADAMANTITE_BIGGER:
				if (!this.adamantite_loaded)
				{
					this.adamantite_loaded = true;
					this.adamantite_count = varbitChanged.getValue();
					return new BlastMineVarbitChangeInfo(449, 0);
				}
				else
				{
					var previousCount = this.adamantite_count;
					var newCount = varbitChanged.getValue();
					var delta = newCount - previousCount;
					this.adamantite_count = newCount;
					return new BlastMineVarbitChangeInfo(449, delta);
				}

			case VarbitID.LOVAKENGJ_ORE_RUNITE_BIGGER:
				if (!this.runite_loaded)
				{
					this.runite_loaded = true;
					this.runite_count = varbitChanged.getValue();
					return new BlastMineVarbitChangeInfo(451, 0);
				}
				else
				{
					var previousCount = this.runite_count;
					var newCount = varbitChanged.getValue();
					var delta = newCount - previousCount;
					this.runite_count = newCount;
					return new BlastMineVarbitChangeInfo(451, delta);
				}

			default:
				return new BlastMineVarbitChangeInfo(1, 0);
		}
	}
}
