/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.pubsub;

import com.isode.stroke.elements.DiscoInfo;
import com.isode.stroke.elements.DiscoItems;
import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.Form;
import com.isode.stroke.elements.FormField;
import com.isode.stroke.elements.FormField.Type;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.elements.PubSub;
import com.isode.stroke.elements.PubSubCreate;
import com.isode.stroke.elements.PubSubItem;
import com.isode.stroke.elements.PubSubItems;
import com.isode.stroke.elements.PubSubOwnerAffiliation;
import com.isode.stroke.elements.PubSubOwnerAffiliations;
import com.isode.stroke.elements.PubSubOwnerConfigure;
import com.isode.stroke.elements.PubSubOwnerDefault;
import com.isode.stroke.elements.PubSubOwnerDelete;
import com.isode.stroke.elements.PubSubOwnerPubSub;
import com.isode.stroke.elements.PubSubOwnerPurge;
import com.isode.stroke.elements.PubSubOwnerRedirect;
import com.isode.stroke.elements.PubSubOwnerSubscriptions;
import com.isode.stroke.elements.PubSubPublish;
import com.isode.stroke.elements.PubSubRetract;
import com.isode.stroke.elements.PubSubSubscribe;
import com.isode.stroke.elements.PubSubSubscriptions;
import com.isode.stroke.elements.PubSubUnsubscribe;
import com.isode.stroke.jid.JID;
import com.isode.stroke.queries.GenericRequest;
import com.isode.stroke.signals.Slot2;

public class PubSubTools {
    static void create(Client client, String domain, String node, Slot2<PubSub, ErrorPayload> callback) {
        /* create a node on a pubsub domain */
        
        PubSubCreate create = new PubSubCreate();
        create.setNode(node);
        
        PubSub pubSub = new PubSub();
        pubSub.setPayload(create);
        
        GenericRequest<PubSub> req = new GenericRequest<PubSub>(IQ.Type.Set, new JID(domain), pubSub, client.getIQRouter());
        req.onResponse.connect(callback);
        req.send();
    }
    
    static void delete(Client client, String domain, String node, String redirectUri, Slot2<PubSubOwnerPubSub, ErrorPayload> callback) {
        /* delete a node on a pubsub domain */
        
        PubSubOwnerDelete delete = new PubSubOwnerDelete();
        delete.setNode(node);
        
        if (!redirectUri.isEmpty()) {
            PubSubOwnerRedirect redirect = new PubSubOwnerRedirect();
            redirect.setURI(redirectUri);
            delete.setRedirect(redirect);
        }
        
        PubSubOwnerPubSub pubSub = new PubSubOwnerPubSub();
        pubSub.setPayload(delete);
        
        GenericRequest<PubSubOwnerPubSub> req = new GenericRequest<PubSubOwnerPubSub>(IQ.Type.Set, new JID(domain), pubSub, client.getIQRouter());
        req.onResponse.connect(callback);
        req.send();
    }
    
    static void itemList(Client client, String domain, Slot2<DiscoItems, ErrorPayload> callback) {
        /* use disco to list the nodes on a pubsub domain */
        
        DiscoItems disco = new DiscoItems();
        
        GenericRequest<DiscoItems> req = new GenericRequest<DiscoItems>(IQ.Type.Get, new JID(domain), disco, client.getIQRouter());
        req.onResponse.connect(callback);
        req.send();
    }
    
    static void featureList(Client client, String domain, Slot2<DiscoInfo, ErrorPayload> callback) {
        /* use disco to list the features of the domain */
        
        DiscoInfo disco = new DiscoInfo();
        
        GenericRequest<DiscoInfo> req = new GenericRequest<DiscoInfo>(IQ.Type.Get, new JID(domain), disco, client.getIQRouter());
        req.onResponse.connect(callback);
        req.send();
    }
    
    static void subscriptionList(Client client, String domain, String node, Slot2<PubSub, ErrorPayload> callback) {
        /* list subscriptions on a pubsub node */
        
        PubSubSubscriptions subscriptions = new PubSubSubscriptions();
        subscriptions.setNode(node);
        
        PubSub pubsub = new PubSub();
        pubsub.setPayload(subscriptions);
        
        GenericRequest<PubSub> listreq = new GenericRequest<PubSub>(IQ.Type.Get, new JID(domain), pubsub, client.getIQRouter());
        listreq.onResponse.connect(callback);
        listreq.send();
    }
    
    static void ownerSubscriptionlist(Client client, String domain, String node, Slot2<PubSubOwnerPubSub, ErrorPayload> callback) {
        /* list subscriptions on a pubsub node */
        
        PubSubOwnerSubscriptions ownerSubscription = new PubSubOwnerSubscriptions();
        ownerSubscription.setNode(node);
        
        PubSubOwnerPubSub pubsub = new PubSubOwnerPubSub();
        pubsub.setPayload(ownerSubscription);
        
        GenericRequest<PubSubOwnerPubSub> listreq = new GenericRequest<PubSubOwnerPubSub>(IQ.Type.Get, new JID(domain), pubsub, client.getIQRouter());
        listreq.onResponse.connect(callback);
        listreq.send();
    }
    
    static void subscribe(Client client, String domain, String node, Slot2<PubSub, ErrorPayload> callback) {
        /* subscribe to a pubsub node */
        
        PubSubSubscribe subscribe = new PubSubSubscribe();
        subscribe.setJID(client.getJID());
        subscribe.setNode(node);
        
        PubSub pubsub = new PubSub();
        pubsub.setPayload(subscribe);
        
        GenericRequest<PubSub> subreq = new GenericRequest<PubSub>(IQ.Type.Set, new JID(domain), pubsub, client.getIQRouter());
        subreq.onResponse.connect(callback);
        subreq.send();
    }
    
    static void unsubscribe(Client client, String domain, String node, Slot2<PubSub, ErrorPayload> callback) {
        /* ubsubscribe from a pubsub node */
        
        PubSubUnsubscribe subscribe = new PubSubUnsubscribe();
        subscribe.setJID(client.getJID());
        subscribe.setNode(node);
        
        PubSub pubsub = new PubSub();
        pubsub.setPayload(subscribe);
        
        GenericRequest<PubSub> subreq = new GenericRequest<PubSub>(IQ.Type.Set, new JID(domain), pubsub, client.getIQRouter());
        subreq.onResponse.connect(callback);
        subreq.send();
    }
    
    static void ownerConfigure(Client client, String domain, String node, Form form, Slot2<PubSubOwnerPubSub, ErrorPayload> callback) {
        /* set/get the current configuration for a node on a pubsub domain */
        
        PubSubOwnerConfigure config = new PubSubOwnerConfigure();
        config.setNode(node);
        
        IQ.Type type;
        if (form == null) {
            type = IQ.Type.Get; /* configuration request */
            config.setData(new Form());
        } else {
            type = IQ.Type.Set; /* setting the configuration */
            config.setData(form);
        }
        
        PubSubOwnerPubSub pubSub = new PubSubOwnerPubSub();
        pubSub.setPayload(config);
        
        GenericRequest<PubSubOwnerPubSub> req = new GenericRequest<PubSubOwnerPubSub>(type, new JID(domain), pubSub, client.getIQRouter());
        req.onResponse.connect(callback);
        req.send();
    }
    
    static void ownerConfigure(final Client client, final String domain, final String node, final String parameter, final String newValue, final Slot2<PubSubOwnerPubSub, ErrorPayload> callback) {
        /* change a parameter for a node on a pubsub domain */
        
        ownerConfigure(client, domain, node, null, new Slot2<PubSubOwnerPubSub, ErrorPayload>() { /* make a request to get the current configuration */
            public void call(PubSubOwnerPubSub pubSub, ErrorPayload error) {
                if (pubSub == null) {
                    callback.call(null, new ErrorPayload());
                }
                PubSubOwnerConfigure config = (PubSubOwnerConfigure)pubSub.getPayload();
                Form form = config.getData();
                for (FormField field : form.getFields()) {
                    if (field.getName().equals(parameter)) {
                        if (field.getType() == Type.TEXT_SINGLE_TYPE) { /* find and update the specified parameter */
                            FormField fieldText = field;
                            fieldText.addValue(newValue);
                        } else if (field.getType() == Type.LIST_SINGLE_TYPE) {
                            FormField fieldList = field;
                            fieldList.addValue(newValue);
                        } else if (field.getType() == Type.BOOLEAN_TYPE) {
                            FormField fieldBoolean = field;
                            fieldBoolean.setBoolValue(newValue.equals("1"));
                        }
                    }
                }
                ownerConfigure(client, domain, node, form, callback); /* request the configuration to be changed */
            }
        });
    }
    
    static void ownerDefaultConfiguration(Client client, String domain, final Slot2<Form, ErrorPayload> callback) {
        /* retrieve the default configuration for nodes on the domain */
        
        PubSubOwnerDefault ownerDefault = new PubSubOwnerDefault();
        ownerDefault.setData(new Form());
        
        PubSubOwnerPubSub pubSub = new PubSubOwnerPubSub();
        pubSub.setPayload(ownerDefault);
        
        GenericRequest<PubSubOwnerPubSub> req = new GenericRequest<PubSubOwnerPubSub>(IQ.Type.Get, new JID(domain), pubSub, client.getIQRouter());
        req.onResponse.connect(new Slot2<PubSubOwnerPubSub, ErrorPayload>() {
            public void call(PubSubOwnerPubSub pubSub, ErrorPayload error) {
                if (pubSub!=null && pubSub.getPayload()!=null) {
                    PubSubOwnerDefault defaultConfig = (PubSubOwnerDefault)pubSub.getPayload();
                    callback.call(defaultConfig.getData(), error);
                } else {
                    callback.call(null, error);
                }
                
            }
        });
        req.send();
    }
    
    static void ownerSetAffiliations(Client client, String domain, String node, JID subscriber, PubSubOwnerAffiliation.Type type, Slot2<PubSubOwnerPubSub, ErrorPayload> callback) {
        /* set the affiliations for a subscriber to a node on a domain */
        
        PubSubOwnerAffiliation affiliation = new PubSubOwnerAffiliation();
        affiliation.setJID(subscriber);
        affiliation.setType(type);
        
        PubSubOwnerAffiliations affiliations = new PubSubOwnerAffiliations();
        affiliations.setNode(node);
        affiliations.addAffiliation(affiliation);
        
        PubSubOwnerPubSub pubSub = new PubSubOwnerPubSub();
        pubSub.setPayload(affiliations);
        
        GenericRequest<PubSubOwnerPubSub> req = new GenericRequest<PubSubOwnerPubSub>(IQ.Type.Set, new JID(domain), pubSub, client.getIQRouter());
        req.onResponse.connect(callback);
        req.send();
    }
    
    static void ownerGetAffiliations(Client client, String domain, String node, Slot2<PubSubOwnerPubSub, ErrorPayload> callback) {
        /* get the affiliations for a subscriber to a node on a domain */
        
        PubSubOwnerAffiliations affiliations = new PubSubOwnerAffiliations();
        affiliations.setNode(node);
        
        PubSubOwnerPubSub pubSub = new PubSubOwnerPubSub();
        pubSub.setPayload(affiliations);
        
        GenericRequest<PubSubOwnerPubSub> req = new GenericRequest<PubSubOwnerPubSub>(IQ.Type.Get, new JID(domain), pubSub, client.getIQRouter());
        req.onResponse.connect(callback);
        req.send();
    }
    
    static void publish(Client client, String domain, String node, String id, Payload payload, Slot2<PubSubPublish, ErrorPayload> callback) {
        /* publish some data to a node */
        
        PubSubItem items = new PubSubItem();
        items.addData(payload);
        items.setID(id);
        
        PubSubPublish publish = new PubSubPublish();
        publish.addItem(items);
        publish.setNode(node);
        
        PubSub pubSub = new PubSub();
        pubSub.setPayload(publish);
        
        GenericRequest<PubSubPublish> req = new GenericRequest<PubSubPublish>(IQ.Type.Set, new JID(domain), pubSub, client.getIQRouter());
        req.onResponse.connect(callback);
        req.send();
    }
    
    static void retract(Client client, String domain, String node, String id, boolean notify, Slot2<PubSub, ErrorPayload> callback) {
        /* delete an item from a node */
        
        PubSubItem item = new PubSubItem();
        item.setID(id);
        
        PubSubRetract retract = new PubSubRetract();
        retract.setNode(node);
        retract.addItem(item);
        retract.setNotify(notify);
        
        PubSub pubSub = new PubSub();
        pubSub.setPayload(retract);
        
        GenericRequest<PubSub> req = new GenericRequest<PubSub>(IQ.Type.Set, new JID(domain), pubSub, client.getIQRouter());
        req.onResponse.connect(callback);
        req.send();
    }
    
    static void getItems(Client client, String domain, String node, long maxResults, Slot2<PubSub, ErrorPayload> callback) {
        /* retrieve maxResults items from a node */
        
        PubSubItems items = new PubSubItems();
        items.setNode(node);
        items.setMaximumItems(maxResults);
        
        PubSub pubSub = new PubSub();
        pubSub.setPayload(items);
        
        GenericRequest<PubSub> req = new GenericRequest<PubSub>(IQ.Type.Get, new JID(domain), pubSub, client.getIQRouter());
        req.onResponse.connect(callback);
        req.send();
    }
    
    static void getItem(Client client, String domain, String node, String id, Slot2<PubSub, ErrorPayload> callback) {
        /* retrieve a specific item from a node */
        
        PubSubItem pubSubItem = new PubSubItem();
        pubSubItem.setID(id);
        
        PubSubItems items = new PubSubItems();
        items.setNode(node);
        items.addItem(pubSubItem);
        
        PubSub pubSub = new PubSub();
        pubSub.setPayload(items);
        
        GenericRequest<PubSub> req = new GenericRequest<PubSub>(IQ.Type.Get, new JID(domain), pubSub, client.getIQRouter());
        req.onResponse.connect(callback);
        req.send();
    }
    
    static void purge(Client client, String domain, String node, Slot2<PubSubOwnerPubSub, ErrorPayload> callback) {
        /* purge all items on a node */
        
        PubSubOwnerPurge purge = new PubSubOwnerPurge();
        purge.setNode(node);
        
        PubSubOwnerPubSub pubSub = new PubSubOwnerPubSub();
        pubSub.setPayload(purge);
        
        GenericRequest<PubSubOwnerPubSub> req = new GenericRequest<PubSubOwnerPubSub>(IQ.Type.Get, new JID(domain), pubSub, client.getIQRouter());
        req.onResponse.connect(callback);
        req.send();
    }
}
