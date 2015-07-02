/*
 * Copyright (c) 2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.disco;

import com.isode.stroke.base.Tristate;
import com.isode.stroke.elements.DiscoInfo;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.jid.JID;
import com.isode.stroke.disco.EntityCapsProvider;
import com.isode.stroke.presence.PresenceOracle;
//import com.isode.stroke.filetransfer.FileTransferManager;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

public class FeatureOracle {

	private EntityCapsProvider capsProvider_;
	private PresenceOracle presenceOracle_;

	public FeatureOracle(EntityCapsProvider capsProvider, PresenceOracle presenceOracle) {
		this.capsProvider_ = capsProvider;
		this.presenceOracle_ = presenceOracle;
	}

	/**
	* To PORT : FileTransfer.
	*/
	/*public Tristate isFileTransferSupported(JID jid) {
		DiscoInfo discoInfo = getDiscoResultForJID(jid);
		if (discoInfo != null) {
			return FileTransferManager.isSupportedBy(discoInfo) ? Tristate.Yes : Tristate.No;
		}
		else {
			return Tristate.Maybe;
		}
	}*/

	public Tristate isMessageReceiptsSupported(JID jid) {
		return isFeatureSupported(jid, DiscoInfo.MessageDeliveryReceiptsFeature);
	}

	public Tristate isMessageCorrectionSupported(JID jid) {
		return isFeatureSupported(jid, DiscoInfo.MessageCorrectionFeature);
	}

	/**
	 * @brief getDiscoResultForJID returns a  shared reference to a DiscoInfo representing features supported by the jid.
	 * @param jid The JID to return the DiscoInfo for.
	 * @return DiscoResult.
	 */
	private DiscoInfo getDiscoResultForJID(JID jid) {
		DiscoInfo discoInfo;
		if (jid.isBare()) {
			// Calculate the common subset of disco features of all available results and return that.
			Collection<Presence> availablePresences =  presenceOracle_.getAllPresence(jid);

			boolean commonFeaturesInitialized = false;
			List<String> commonFeatures = new ArrayList<String>();
			for(Presence presence : availablePresences) {
				DiscoInfo presenceDiscoInfo = capsProvider_.getCaps(presence.getFrom());
				if (presenceDiscoInfo != null) {
					List<String> features = presenceDiscoInfo.getFeatures();
					if (!commonFeaturesInitialized) {
						commonFeatures = features;
						commonFeaturesInitialized = true;
					}
					else {
						List<String> featuresToRemove = new ArrayList<String>();
						for(String feature : commonFeatures) {
							if(!features.contains(feature)) {
								featuresToRemove.add(feature);
							}
						}
						for(String featureToRemove : featuresToRemove) {
							while(commonFeatures.contains(featureToRemove)) {
								commonFeatures.remove(featureToRemove);
							}
						}
					}
				}
			}
			discoInfo = new DiscoInfo();

			for(String commonFeature : commonFeatures) {
				discoInfo.addFeature(commonFeature);
			}
		}
		else {
			// Return the disco result of the full JID.
			discoInfo = capsProvider_.getCaps(jid);
		}

		return discoInfo;
	}

	private Tristate isFeatureSupported(JID jid, String feature) {
		DiscoInfo discoInfo = getDiscoResultForJID(jid);
		if (discoInfo != null) {
			return discoInfo.hasFeature(feature) ? Tristate.Yes : Tristate.No;
		}
		else {
			return Tristate.Maybe;
		}
	}
}